package com.hjj.apiserver.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.benmanes.caffeine.cache.Caffeine
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.domain.financial.FinancialProduct
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
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
    fun redisCacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager =
        RedisCacheManagerBuilder.fromCacheWriter(
            RedisCacheWriter.nonLockingRedisCacheWriter(
                redisConnectionFactory,
                UnlinkScanBatchStrategy(batchSize = redisProperties.batchSize),
            ),
        ).cacheDefaults(this.createRedisCacheConfiguration(Jackson2JsonRedisSerializer(Any::class.java)))
            .withInitialCacheConfigurations(
                mapOf(
                    FINANCIAL_PRODUCTS to
                        this.createRedisCacheConfiguration(
                            typeReferenceJackson2JsonRedisSerializer(object : TypeReference<List<FinancialProduct>>() {}),
                            redisTtl = 300L,
                        ),
                    FINANCIAL_PRODUCTS_EXISTS_NEXT_PAGE to
                        this.createRedisCacheConfiguration(
                            jackson2JsonRedisSerializer(Boolean::class.java), redisTtl = 300L,
                        ),
                    FINANCIAL_PRODUCT to
                        this.createRedisCacheConfiguration(
                            jackson2JsonRedisSerializer(FinancialProduct::class.java), redisTtl = 300L,
                        ),
                ),
            )
            .transactionAware()
            .build()

    private fun <T> jackson2JsonRedisSerializer(clazz: Class<T>): Jackson2JsonRedisSerializer<T> {
        val mapper =
            ObjectMapper()
                .registerModules(
                    JavaTimeModule(),
                    KotlinModule.Builder().build(),
                )
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT)

        val javaType = mapper.typeFactory.constructType(clazz)
        return Jackson2JsonRedisSerializer(mapper, javaType)
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(
            RedisStandaloneConfiguration(redisProperties.host, redisProperties.port).apply {
                database = redisProperties.database
            },
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofMillis(redisProperties.commandTimeout)).build(),
        )

    private fun <T> typeReferenceJackson2JsonRedisSerializer(typeReference: TypeReference<T>): Jackson2JsonRedisSerializer<T> {
        val mapper =
            ObjectMapper()
                .registerModules(
                    JavaTimeModule(),
                    KotlinModule.Builder().build(),
                )
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT)

        val javaType = mapper.typeFactory.constructType(typeReference)
        return Jackson2JsonRedisSerializer(mapper, javaType)
    }

    private fun createRedisCacheConfiguration(
        serializer: RedisSerializer<*>,
        redisTtl: Long = DEFAULT_TTL,
    ): RedisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(redisTtl))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()),
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer),
            )
}
