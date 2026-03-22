package com.hjj.apiserver.application.port.out.auth.model

import java.time.OffsetDateTime

data class IssuedRefreshSession(
    val sessionId: String,
    val refreshToken: String,
    val expiresAt: OffsetDateTime,
)

sealed interface RefreshTokenRotationResult {
    data class Rotated(
        val sessionId: String,
        val userId: Long,
        val refreshToken: String,
        val expiresAt: OffsetDateTime,
    ) : RefreshTokenRotationResult

    data class Rejected(
        val reason: RefreshTokenRejectionReason,
        val userId: Long? = null,
    ) : RefreshTokenRotationResult
}

enum class RefreshTokenRejectionReason {
    INVALID,
    REUSED,
    REVOKED,
    EXPIRED,
}
