package com.hjj.apiserver.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.hjj.apiserver.common.JwtProvider
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfig {

    @Bean(name = ["caffeineCacheManager"])
    fun cacheManager(): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(
            Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .expireAfterWrite(JwtProvider.REFRESH_TOKEN_VALID_MILLISECONDS, TimeUnit.MILLISECONDS)
                .weakKeys()
                .recordStats()
        )
        return caffeineCacheManager
    }
}