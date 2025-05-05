package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime

data class RefreshToken(
    val jti: String,
    val userId: Long,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val revoked: Boolean,
    val userAgent: String?,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)
