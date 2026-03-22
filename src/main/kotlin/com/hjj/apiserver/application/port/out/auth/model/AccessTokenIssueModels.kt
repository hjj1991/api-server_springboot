package com.hjj.apiserver.application.port.out.auth.model

import java.time.OffsetDateTime

data class AccessTokenIssueCommand(
    val userId: Long,
    val tokenVersion: Long,
    val sessionId: String,
    val roles: List<String>,
    val issuedAt: OffsetDateTime,
)

data class IssuedAccessToken(
    val token: String,
    val jti: String,
    val issuedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
)
