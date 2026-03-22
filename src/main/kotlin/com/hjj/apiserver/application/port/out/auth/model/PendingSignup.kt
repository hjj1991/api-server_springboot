package com.hjj.apiserver.application.port.out.auth.model

import java.time.OffsetDateTime

data class PendingSignup(
    val displayName: String,
    val normalizedLoginId: String,
    val normalizedEmail: String,
    val emailLookupHash: String,
    val passwordHash: String,
    val requestedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
)
