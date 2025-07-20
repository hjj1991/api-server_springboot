package com.hjj.apiserver.config

import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Configuration
class CacheKeyConfig {
    companion object {
        const val PARAMS_LOCAL_DATE = "paramsLocalDate"
    }

    @Bean(PARAMS_LOCAL_DATE)
    fun paramsLocalDateCacheKey(): KeyGenerator =
        KeyGenerator { _, _, params ->
            "${ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}"
        }
}
