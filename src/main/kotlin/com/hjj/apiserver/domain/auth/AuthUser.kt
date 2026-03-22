package com.hjj.apiserver.domain.auth

import java.time.OffsetDateTime

data class AuthUser(
    val userId: Long,
    val displayName: String,
    val emailCiphertext: String?,
    val emailLookupHash: String?,
    val emailVerifiedAt: OffsetDateTime?,
    val tokenVersion: Long,
    val status: UserStatus,
)

data class AuthenticatedUserAccount(
    val user: AuthUser,
    val roleNames: List<RoleName>,
)
