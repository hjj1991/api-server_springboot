package com.hjj.apiserver.adapter.out.redis.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.port.out.auth.RefreshTokenSessionPort
import com.hjj.apiserver.application.port.out.auth.model.IssuedRefreshSession
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRejectionReason
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRotationResult
import com.hjj.apiserver.application.service.auth.AuthCryptoService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

@Component
class RefreshSessionRedisAdapter(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val authCryptoService: AuthCryptoService,
) : RefreshTokenSessionPort {
    override fun issue(
        userId: Long,
        issuedAt: OffsetDateTime,
        expiresAt: OffsetDateTime,
    ): IssuedRefreshSession {
        val sessionId = UUID.randomUUID().toString()
        val refreshToken = authCryptoService.createOpaqueToken()
        val tokenHash = authCryptoService.createOpaqueTokenHash(refreshToken)
        val payload =
            StoredRefreshSession(
                sessionId = sessionId,
                userId = userId,
                currentTokenHash = tokenHash,
                previousTokenHash = null,
                issuedAt = issuedAt,
                expiresAt = expiresAt,
                revokedAt = null,
                lastRotatedAt = null,
            )

        persist(payload = payload, knownHashes = setOf(tokenHash), now = issuedAt)

        return IssuedRefreshSession(
            sessionId = sessionId,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
        )
    }

    override fun rotate(
        refreshToken: String,
        rotatedAt: OffsetDateTime,
        expiresAt: OffsetDateTime,
    ): RefreshTokenRotationResult {
        val providedTokenHash = authCryptoService.createOpaqueTokenHash(refreshToken)
        val sessionId = stringRedisTemplate.opsForValue().get(lookupKey(providedTokenHash))
            ?: return RefreshTokenRotationResult.Rejected(RefreshTokenRejectionReason.INVALID)
        val payload = findBySessionId(sessionId)
            ?: return RefreshTokenRotationResult.Rejected(RefreshTokenRejectionReason.INVALID)

        if (payload.revokedAt != null) {
            revokeSession(payload)
            return RefreshTokenRotationResult.Rejected(RefreshTokenRejectionReason.REVOKED, payload.userId)
        }

        if (!payload.expiresAt.isAfter(rotatedAt)) {
            revokeSession(payload)
            return RefreshTokenRotationResult.Rejected(RefreshTokenRejectionReason.EXPIRED, payload.userId)
        }

        if (payload.currentTokenHash != providedTokenHash) {
            revokeAll(payload.userId, rotatedAt)
            return RefreshTokenRotationResult.Rejected(RefreshTokenRejectionReason.REUSED, payload.userId)
        }

        val newRefreshToken = authCryptoService.createOpaqueToken()
        val newTokenHash = authCryptoService.createOpaqueTokenHash(newRefreshToken)
        val updatedPayload =
            payload.copy(
                previousTokenHash = payload.currentTokenHash,
                currentTokenHash = newTokenHash,
                expiresAt = expiresAt,
                lastRotatedAt = rotatedAt,
            )

        persist(
            payload = updatedPayload,
            knownHashes = setOfNotNull(updatedPayload.currentTokenHash, updatedPayload.previousTokenHash),
            now = rotatedAt,
            obsoletePreviousHash = payload.previousTokenHash,
        )

        return RefreshTokenRotationResult.Rotated(
            sessionId = updatedPayload.sessionId,
            userId = updatedPayload.userId,
            refreshToken = newRefreshToken,
            expiresAt = updatedPayload.expiresAt,
        )
    }

    override fun revoke(
        refreshToken: String,
        revokedAt: OffsetDateTime,
    ) {
        val tokenHash = authCryptoService.createOpaqueTokenHash(refreshToken)
        val sessionId = stringRedisTemplate.opsForValue().get(lookupKey(tokenHash)) ?: return
        findBySessionId(sessionId)?.let { revokeSession(it) } ?: stringRedisTemplate.delete(lookupKey(tokenHash))
    }

    override fun revokeAll(
        userId: Long,
        revokedAt: OffsetDateTime,
    ) {
        stringRedisTemplate.opsForSet().members(userSessionsKey(userId)).orEmpty()
            .forEach { sessionId ->
                findBySessionId(sessionId)?.let { revokeSession(it) }
            }
        stringRedisTemplate.delete(userSessionsKey(userId))
    }

    private fun persist(
        payload: StoredRefreshSession,
        knownHashes: Set<String>,
        now: OffsetDateTime,
        obsoletePreviousHash: String? = null,
    ) {
        val ttl = remainingTtl(now, payload.expiresAt)
        stringRedisTemplate.opsForValue().set(sessionKey(payload.sessionId), objectMapper.writeValueAsString(payload), ttl)
        knownHashes.forEach { tokenHash ->
            stringRedisTemplate.opsForValue().set(lookupKey(tokenHash), payload.sessionId, ttl)
        }
        obsoletePreviousHash
            ?.takeIf { it !in knownHashes }
            ?.let { stringRedisTemplate.delete(lookupKey(it)) }
        stringRedisTemplate.opsForSet().add(userSessionsKey(payload.userId), payload.sessionId)
        stringRedisTemplate.expire(userSessionsKey(payload.userId), ttl)
    }

    private fun revokeSession(payload: StoredRefreshSession) {
        stringRedisTemplate.delete(sessionKey(payload.sessionId))
        stringRedisTemplate.delete(lookupKey(payload.currentTokenHash))
        payload.previousTokenHash?.let { stringRedisTemplate.delete(lookupKey(it)) }
        stringRedisTemplate.opsForSet().remove(userSessionsKey(payload.userId), payload.sessionId)
    }

    private fun findBySessionId(sessionId: String): StoredRefreshSession? =
        stringRedisTemplate.opsForValue().get(sessionKey(sessionId))
            ?.let { objectMapper.readValue(it, StoredRefreshSession::class.java) }

    private fun remainingTtl(
        now: OffsetDateTime,
        expiresAt: OffsetDateTime,
    ): Duration =
        Duration.between(now, expiresAt).let { ttl ->
            if (ttl.isNegative || ttl.isZero) Duration.ofSeconds(1) else ttl
        }

    private fun sessionKey(sessionId: String): String = "$SESSION_KEY_PREFIX:$sessionId"

    private fun lookupKey(tokenHash: String): String = "$LOOKUP_KEY_PREFIX:$tokenHash"

    private fun userSessionsKey(userId: Long): String = "$USER_SESSIONS_KEY_PREFIX:$userId"

    private companion object {
        const val SESSION_KEY_PREFIX = "auth:refresh:session"
        const val LOOKUP_KEY_PREFIX = "auth:refresh:lookup"
        const val USER_SESSIONS_KEY_PREFIX = "auth:user:sessions"
    }
}

data class StoredRefreshSession(
    val sessionId: String,
    val userId: Long,
    val currentTokenHash: String,
    val previousTokenHash: String?,
    val issuedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val revokedAt: OffsetDateTime?,
    val lastRotatedAt: OffsetDateTime?,
)
