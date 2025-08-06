package com.hjj.apiserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${nlp.api.url}")
    private val nlpApiUrl: String,
) {

    @Bean
    fun nlpWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(nlpApiUrl)
            .build()
    }
}
