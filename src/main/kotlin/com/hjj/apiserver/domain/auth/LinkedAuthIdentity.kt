package com.hjj.apiserver.domain.auth

import java.time.OffsetDateTime

data class LinkedAuthIdentity(
    val providerType: AuthProviderType,
    val loginId: String?,
    val linkedAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?,
)
