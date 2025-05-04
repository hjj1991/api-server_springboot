package com.hjj.apiserver.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.benmanes.caffeine.cache.Caffeine
import com.hjj.apiserver.common.JwtProvider
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfig(
    private val redisProperties: RedisProperties,
) {
    companion object {
        const val DEFAULT_TTL = 5L
        const val CAFFEINE_CACHE_MANAGER = "caffeineCacheManager"
        const val REDIS_CACHE_MANAGER = "redisCacheManager"
        const val FINANCIAL_PRODUCTS = "financialProducts"
        const val FINANCIAL_PRODUCTS_EXISTS_NEXT_PAGE = "financialProductsExistsNextPage"
        const val FINANCIAL_PRODUCT = "financialProduct"
    }

    @Bean(name = [CAFFEINE_CACHE_MANAGER])
    fun caffeineCacheManager(): CacheManager =
        CaffeineCacheManager().apply {
            setCaffeine(
                Caffeine.newBuilder()
                    .initialCapacity(200)
                    .maximumSize(500)
                    .expireAfterWrite(JwtProvider.REFRESH_TOKEN_VALID_MILLISECONDS, TimeUnit.MILLISECONDS)
                    .weakKeys()
                    .recordStats(),
            )
        }

    @Primary
    @Bean(name = [REDIS_CACHE_MANAGER])
    fun redisCacheManager(
        redisConnectionFactory: RedisConnectionFactory,
        redisSerializer: RedisSerializer<Any>,
    ): CacheManager =
        RedisCacheManagerBuilder.fromCacheWriter(
            RedisCacheWriter.nonLockingRedisCacheWriter(
                redisConnectionFactory,
                UnlinkScanBatchStrategy(batchSize = redisProperties.batchSize),
            ),
        ).cacheDefaults(this.createRedisCacheConfiguration(redisSerializer = redisSerializer))
            .withInitialCacheConfigurations(
                mapOf(
                    FINANCIAL_PRODUCTS to this.createRedisCacheConfiguration(redisSerializer = redisSerializer, redisTtl = 300L),
                    FINANCIAL_PRODUCTS_EXISTS_NEXT_PAGE to this.createRedisCacheConfiguration(redisSerializer = redisSerializer, redisTtl = 300L),
                ),
            )
            .transactionAware()
            .build()

    @Bean
    fun redisSerializer(): RedisSerializer<Any> =
        GenericJackson2JsonRedisSerializer(
            ObjectMapper()
                .registerModules(
                    JavaTimeModule(),
                    KotlinModule.Builder().build(),
                )
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT),
        )

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(
            RedisStandaloneConfiguration(redisProperties.host, redisProperties.port).apply {
                database = redisProperties.database
            },
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofMillis(redisProperties.commandTimeout)).build(),
        )

    @Bean
    fun redisTemplate(
        connectionFactory: LettuceConnectionFactory,
        redisSerializer: RedisSerializer<Any>,
    ): RedisTemplate<String, Any> {
        val keySerializer = StringRedisSerializer()
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            setKeySerializer(keySerializer)
            setValueSerializer(redisSerializer)
        }
    }

    private fun createRedisCacheConfiguration(
        redisSerializer: RedisSerializer<Any>,
        redisTtl: Long = DEFAULT_TTL,
    ): RedisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(redisTtl))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
}
