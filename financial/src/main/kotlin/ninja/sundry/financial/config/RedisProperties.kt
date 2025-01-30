package com.hjj.apiserver.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.data.redis")
class RedisProperties(
    val host: String,
    val port: Int,
    val database: Int,
    val connectTimeout: Int,
    val commandTimeout: Long,
    val batchSize: Int,
)
