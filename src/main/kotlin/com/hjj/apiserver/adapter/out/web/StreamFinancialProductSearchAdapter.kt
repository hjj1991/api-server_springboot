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

        // 하트비트 스케줄링
        val heartbeatFuture = sseTaskScheduler.scheduleAtFixedRate(
            {
                try {
                    emitter.send(SseEmitter.event().comment("keepalive"))
                } catch (_: Exception) {
                    // 연결이 끊어졌을 때는 무시
                }
            },
            Duration.ofSeconds(15)
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

            // 데이터 전처리: 줄바꿈과 마크다운 정리
            val processedData = formatForFrontend(data)
            
            val eventBuilder = SseEmitter.event().apply {
                sse.event()?.let { name(it) }
                sse.id()?.let { id(it) }
                data(processedData)
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

    /**
     * 프론트엔드에서 제대로 표시될 수 있도록 텍스트 포맷 정리
     */
    private fun formatForFrontend(data: String): String {
        return data
            // 줄바꿈 정규화만
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("```", "\n```")
        
            // 문장 끝에만 줄바꿈 추가 (마크다운 구조는 건드리지 않음)
            .replace(Regex("([.!?])(?![\\s\n])")) { "${it.value}\n" }
        
            // 연속된 줄바꿈 정리
            .replace(Regex("\n{3,}"), "\n\n")
        
            .trim()
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
}
