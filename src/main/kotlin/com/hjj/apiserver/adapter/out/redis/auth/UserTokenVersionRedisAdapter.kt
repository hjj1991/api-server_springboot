package com.hjj.apiserver.adapter.out.redis.auth

import com.hjj.apiserver.application.port.out.auth.UserTokenVersionCachePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class UserTokenVersionRedisAdapter(
    private val stringRedisTemplate: StringRedisTemplate,
) : UserTokenVersionCachePort {
    override fun get(userId: Long): Long? = stringRedisTemplate.opsForValue().get(tokenVersionKey(userId))?.toLongOrNull()

    override fun set(
        userId: Long,
        tokenVersion: Long,
        ttl: Duration,
    ) {
        stringRedisTemplate.opsForValue().set(
            tokenVersionKey(userId),
            tokenVersion.toString(),
            ttl,
        )
    }

    override fun delete(userId: Long) {
        stringRedisTemplate.delete(tokenVersionKey(userId))
    }

    private fun tokenVersionKey(userId: Long): String = "$TOKEN_VERSION_PREFIX:$userId"

    private companion object {
        const val TOKEN_VERSION_PREFIX = "auth:user:token-version"
    }
}
