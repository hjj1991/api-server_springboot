package com.hjj.apiserver.adapter.out.redis.auth

import com.hjj.apiserver.application.port.out.auth.AccessTokenDenylistPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Component
class AccessTokenDenylistRedisAdapter(
    private val stringRedisTemplate: StringRedisTemplate,
    private val clock: Clock,
) : AccessTokenDenylistPort {
    override fun isDenied(jti: String): Boolean = stringRedisTemplate.hasKey(denyKey(jti))

    override fun deny(
        jti: String,
        expiresAt: Instant,
    ) {
        val ttl = Duration.between(clock.instant(), expiresAt)
        if (ttl.isNegative || ttl.isZero) {
            return
        }
        stringRedisTemplate.opsForValue().set(denyKey(jti), "1", ttl)
    }

    private fun denyKey(jti: String): String = "$ACCESS_DENYLIST_PREFIX:$jti"

    private companion object {
        const val ACCESS_DENYLIST_PREFIX = "auth:deny:access"
    }
}
