package com.hjj.apiserver.adapter.out.web

import com.hjj.apiserver.application.port.out.financial.StreamFinancialProductSearchPort
import mu.two.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.IOException
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class StreamFinancialProductSearchAdapter(
    private val webClient: WebClient,
    @Value("\${nlp.api.url}") private val nlpApiUrl: String,
    private val sseTaskScheduler: ThreadPoolTaskScheduler,
) : StreamFinancialProductSearchPort {

    companion object {
        private const val EMITTER_TIMEOUT_MS = 60_000L
        private const val HEARTBEAT_INTERVAL_SECONDS = 15L
        private const val BACKPRESSURE_BUFFER_SIZE = 256
        private const val KEEPALIVE_COMMENT = "keepalive"
    }

    override fun searchFinancialProduct(query: String): SseEmitter {
        val emitter = SseEmitter(EMITTER_TIMEOUT_MS)

        // 업스트림 플럭스 생성
        val flux: Flux<ServerSentEvent<String>> = webClient.get()
            .uri("$nlpApiUrl/products/search/stream?query={query}", query)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(object : ParameterizedTypeReference<ServerSentEvent<String>>() {})
            .onBackpressureBuffer(BACKPRESSURE_BUFFER_SIZE)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError { error ->
                log.error(error) { "Upstream SSE error for query: $query" }
            }

        // 하트비트 스케줄링
        val heartbeatFuture = sseTaskScheduler.scheduleAtFixedRate(
            {
                try {
                    emitter.send(SseEmitter.event().comment(KEEPALIVE_COMMENT))
                } catch (_: Exception) {
                    // 연결이 끊어졌을 때는 무시
                }
            },
            Duration.ofSeconds(HEARTBEAT_INTERVAL_SECONDS)
        )

        // 스트림 구독
        val subscription = flux.subscribe(
            { sse -> handleSseEvent(emitter, sse) },
            { error -> handleError(emitter, error) },
            { handleCompletion(emitter) },
        )

        setupEmitterCallbacks(emitter, subscription, heartbeatFuture)

        return emitter
    }

    private fun handleSseEvent(emitter: SseEmitter, sse: ServerSentEvent<String>) {
        try {
            val data = sse.data()

            // 로그는 전체 데이터를 다 보여줌
            log.debug { "Received SSE event: event=${sse.event()}, id=${sse.id()}, data=$data" }

            if (data.isNullOrBlank()) {
                log.debug { "Skipping empty SSE event" }
                return
            }

            val eventBuilder = SseEmitter.event().apply {
                sse.event()?.let { name(it) }
                sse.id()?.let { id(it) }
                data(data)
            }

            emitter.send(eventBuilder)
        } catch (e: IOException) {
            log.error(e) { "Failed to send SSE event" }
            emitter.completeWithError(e)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error handling SSE event" }
            emitter.completeWithError(e)
        }
    }

    private fun handleError(emitter: SseEmitter, error: Throwable) {
        when (error) {
            is WebClientResponseException -> {
                log.error { "HTTP error from upstream: ${error.statusCode} - ${error.responseBodyAsString}" }
            }

            is WebClientRequestException -> {
                log.error { "Request error to upstream: ${error.message}" }
            }

            else -> {
                log.error(error) { "Upstream SSE stream error" }
            }
        }
        emitter.completeWithError(error)
    }

    private fun handleCompletion(emitter: SseEmitter) {
        log.debug { "Upstream SSE stream completed successfully. Sending [DONE] event." }
        try {
            // 1. 클라이언트에게 "[DONE]" 메시지를 보내 대화가 끝났음을 알립니다.
            emitter.send(SseEmitter.event().name("message").data("[DONE]"))
        } catch (e: IOException) {
            log.warn(e) { "Failed to send [DONE] event to the client." }
        } finally {
            // 2. "[DONE]" 메시지를 보낸 후, 연결을 정상적으로 종료합니다.
            emitter.complete()
        }
    }

    private fun setupEmitterCallbacks(
        emitter: SseEmitter,
        subscription: Disposable,
        heartbeatFuture: java.util.concurrent.Future<*>
    ) {
        val cleanup = {
            try {
                subscription.dispose()
                heartbeatFuture.cancel(true)
            } catch (e: Exception) {
                log.debug(e) { "Error during cleanup" }
            }
        }

        emitter.onCompletion {
            log.debug { "SSE emitter completed" }
            cleanup()
        }

        emitter.onTimeout {
            log.warn { "SSE emitter timed out" }
            cleanup()
        }

        emitter.onError { error ->
            log.error(error) { "SSE emitter error" }
            cleanup()
        }
    }
}
