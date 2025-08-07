package com.hjj.apiserver.adapter.out.web

import com.hjj.apiserver.application.port.out.financial.StreamFinancialProductSearchPort
import mu.two.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import reactor.core.publisher.Flux
import java.io.IOException
import java.util.concurrent.Executors

private val log = KotlinLogging.logger {}

@Component
class StreamFinancialProductSearchAdapter(
    private val webClient: WebClient,
    @Value("\${nlp.api.url}") private val nlpApiUrl: String,
) : StreamFinancialProductSearchPort {
    override fun searchFinancialProduct(query: String): SseEmitter {
        val emitter = SseEmitter(60000L) // SSE 전용 Emitter 사용
        val executor = Executors.newSingleThreadExecutor()

        emitter.onCompletion { log.debug { "Emitter completed" } }
        emitter.onTimeout { log.warn { "Emitter timed out" } }
        emitter.onError { e -> log.error(e) { "Emitter error" } }

        executor.submit {
            try {
                val flux: Flux<String> = webClient.get()
                    .uri("$nlpApiUrl/products/search/stream?query={query}", query)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(String::class.java)

                log.debug { "Subscribing to Python server stream..." }
                flux.subscribe(
                    { data ->
                        try {
                            log.debug { "Received data chunk from Python: $data" }
                            // SseEmitter.SseEventBuilder를 사용하여 SSE 이벤트 생성
                            val event = SseEmitter.event()
                                .data(data)
                                .name("message") // 이벤트 이름 지정 (선택사항)

                            emitter.send(event)
                        } catch (e: IOException) {
                            log.error(e) { "Error sending data to emitter after receiving from Python" }
                            emitter.completeWithError(e)
                        }
                    },
                    { error ->
                        log.error(error) { "Error from Python server stream" }
                        emitter.completeWithError(error)
                    },
                    {
                        log.debug { "Python server stream completed." }
                        emitter.complete()
                    },
                )
            } catch (e: Exception) {
                log.error(e) { "Exception during WebClient call to Python server" }
                emitter.completeWithError(e)
            } finally {
                executor.shutdown()
            }
        }

        return emitter
    }
}
