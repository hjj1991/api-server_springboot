package com.hjj.apiserver.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.hjj.apiserver.common.advisor.ControllerExceptionLogTrace
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.util.logger
import com.querydsl.jpa.impl.JPAQueryFactory
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager

@Configuration
class ApplicationConfig(
        @Value("\${app.firebase-configuration-file}")
        private val firebaseConfigPath: String
): AuditorAware<Long> {

    private val log = logger()



    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }


    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        modelMapper.configuration.matchingStrategy = MatchingStrategies.STRICT
        return modelMapper
    }

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .filter(ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
                log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
                clientRequest.headers().forEach { name: String?, values: List<String?> ->
                        values.forEach(Consumer { value: String? -> log.info("{}={}", name, value) })
                    }
                Mono.just(clientRequest)
            })
            .build()
    }

    @Bean
    fun jpaQueryFactory(em: EntityManager): JPAQueryFactory {
        return JPAQueryFactory(em)
    }

    @Bean
    fun controllerExceptionLogTrace(): ControllerExceptionLogTrace {
        return ControllerExceptionLogTrace()
    }


    override fun getCurrentAuditor(): Optional<Long> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (null == authentication || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            return Optional.empty()
        }
        val currentUserInfo = authentication.principal as CurrentUserInfo
        return Optional.of(currentUserInfo.userNo)
    }

    @PostConstruct
    fun initialize() {
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(FileInputStream(firebaseConfigPath)))
                .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                log.info("Firebase application has been initializaed")
            }
        } catch (e: IOException) {
            log.error(e.message)
        }
    }

}