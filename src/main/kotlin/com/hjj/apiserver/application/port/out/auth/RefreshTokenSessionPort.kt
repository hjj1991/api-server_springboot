package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.IssuedRefreshSession
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRotationResult
import java.time.OffsetDateTime

interface RefreshTokenSessionPort {
    fun issue(
        userId: Long,
        issuedAt: OffsetDateTime,
        expiresAt: OffsetDateTime,
    ): IssuedRefreshSession

    fun rotate(
        refreshToken: String,
        rotatedAt: OffsetDateTime,
        expiresAt: OffsetDateTime,
    ): RefreshTokenRotationResult

    fun revoke(
        refreshToken: String,
        revokedAt: OffsetDateTime,
    )

    fun revokeAll(
        userId: Long,
        revokedAt: OffsetDateTime,
    )
}
