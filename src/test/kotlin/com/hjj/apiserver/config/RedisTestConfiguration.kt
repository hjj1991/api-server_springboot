package com.hjj.apiserver.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
@EnableConfigurationProperties(RedisProperties::class)
class RedisTestConfiguration {
    companion object {
        // Redis 컨테이너 설정
        val redisContainer: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .apply { start() }
    }

    @Bean
    fun redisProperties(): RedisProperties =
        RedisProperties(
            host = redisContainer.host,
            port = 6379,
            database = 0,
            connectTimeout = 10000,
            commandTimeout = 5000,
            batchSize = 1000,
        )
}
