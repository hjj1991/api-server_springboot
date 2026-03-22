package com.hjj.apiserver.adapter.out.redis.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.port.out.auth.PendingSignupPort
import com.hjj.apiserver.application.port.out.auth.model.PendingSignup
import com.hjj.apiserver.application.service.auth.AuthCryptoService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PendingSignupRedisAdapter(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val authCryptoService: AuthCryptoService,
) : PendingSignupPort {
    override fun save(
        verificationToken: String,
        signup: PendingSignup,
    ) {
        val tokenHash = authCryptoService.createVerificationTokenHash(verificationToken)
        val tokenKey = tokenKey(tokenHash)
        val emailKey = emailKey(signup.emailLookupHash)
        val ttl = Duration.between(signup.requestedAt, signup.expiresAt)

        stringRedisTemplate.opsForValue().get(emailKey)?.let { previousTokenHash ->
            stringRedisTemplate.delete(tokenKey(previousTokenHash))
        }

        stringRedisTemplate.opsForValue().set(tokenKey, objectMapper.writeValueAsString(signup), ttl)
        stringRedisTemplate.opsForValue().set(emailKey, tokenHash, ttl)
    }

    override fun findByVerificationToken(token: String): PendingSignup? {
        val tokenHash = authCryptoService.createVerificationTokenHash(token)
        val payload = stringRedisTemplate.opsForValue().get(tokenKey(tokenHash)) ?: return null
        return objectMapper.readValue(payload, PendingSignup::class.java)
    }

    override fun deleteByVerificationToken(token: String) {
        val tokenHash = authCryptoService.createVerificationTokenHash(token)
        val key = tokenKey(tokenHash)
        val payload = stringRedisTemplate.opsForValue().get(key)
        val pendingSignup = payload?.let { objectMapper.readValue(it, PendingSignup::class.java) }
        stringRedisTemplate.delete(key)
        pendingSignup?.let { stringRedisTemplate.delete(emailKey(it.emailLookupHash)) }
    }

    private fun tokenKey(tokenHash: String): String = "$TOKEN_KEY_PREFIX:$tokenHash"

    private fun emailKey(emailLookupHash: String): String = "$EMAIL_KEY_PREFIX:$emailLookupHash"

    private companion object {
        const val TOKEN_KEY_PREFIX = "auth:signup:token"
        const val EMAIL_KEY_PREFIX = "auth:signup:email"
    }
}
