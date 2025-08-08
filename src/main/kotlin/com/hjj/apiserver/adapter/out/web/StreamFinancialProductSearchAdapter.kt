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

    override fun searchFinancialProduct(query: String): SseEmitter {
        val emitter = SseEmitter(60000L)

        // 업스트림 플럭스 생성
        val flux: Flux<ServerSentEvent<String>> = webClient.get()
            .uri("$nlpApiUrl/products/search/stream?query={query}", query)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(object : ParameterizedTypeReference<ServerSentEvent<String>>() {})
            .onBackpressureBuffer(256)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError { error ->
                log.error(error) { "Upstream SSE error for query: $query" }
            }

        // scheduleAtFixedRate
        val heartbeatFuture = sseTaskScheduler.scheduleAtFixedRate(
            {
                try {
                    emitter.send(SseEmitter.event().comment("keepalive"))
                } catch (_: Exception) {
                }
            },
            Duration.ofSeconds(15)
        )

        val subscription = flux.subscribe(
            { sse -> handleSseEvent(emitter, sse) },
            { error -> handleError(emitter, error) },
            { handleCompletion(emitter) },
        )


        // 리소스 정리를 위한 공통 함수
        setupEmitterCallbacks(emitter, subscription, heartbeatFuture)

        return emitter
    }

    private fun handleSseEvent(emitter: SseEmitter, sse: ServerSentEvent<String>) {
        try {
            val data = sse.data()
            if (data.isNullOrBlank()) {
                log.debug { "Skipping empty SSE event: ${sse.event()} / ${sse.id()}" }
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
        log.error(error) { "Upstream SSE stream error" }
        emitter.completeWithError(error)
    }

    private fun handleCompletion(emitter: SseEmitter) {
        log.debug { "Upstream SSE stream completed successfully" }
        emitter.complete()
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

    private fun SseEmitter.isComplete(): Boolean {
        return try {
            // Spring의 SseEmitter는 완료 상태를 직접 확인할 수 있는 메서드가 없으므로
            // 대신 send 시도로 확인 (더 나은 방법이 있다면 사용)
            this.send(SseEmitter.event().comment(""))
            false
        } catch (e: Exception) {
            true
        }
    }
}
