package com.hjj.apiserver.config

import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import mu.two.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Clock
import java.util.Optional
import java.util.function.Consumer

@ConfigurationPropertiesScan
@EnableJpaAuditing
@Configuration
class ApplicationConfig : AuditorAware<Long> {
    private val log = KotlinLogging.logger {}

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }


    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .filter(
                ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
                    clientRequest.headers().forEach { name: String?, values: List<String?> ->
                        values.forEach(Consumer { value: String? -> log.info("{}={}", name, value) })
                    }
                    Mono.just(clientRequest)
                },
            )
            .build()
    }

    @Bean
    fun jpaQueryFactory(em: EntityManager): JPAQueryFactory {
        return JPAQueryFactory(JPQLTemplates.DEFAULT, em)
    }

    override fun getCurrentAuditor(): Optional<Long> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (null == authentication || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            return Optional.empty()
        }
        val currentUserInfo = authentication.principal as CurrentUserInfo
        return Optional.of(currentUserInfo.userNo)
    }
}
