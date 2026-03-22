package com.hjj.apiserver.application.port.out.auth

import java.time.Duration

interface UserTokenVersionCachePort {
    fun get(userId: Long): Long?

    fun set(
        userId: Long,
        tokenVersion: Long,
        ttl: Duration,
    )

    fun delete(userId: Long)
}
