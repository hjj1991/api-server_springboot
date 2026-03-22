package com.hjj.apiserver.domain.auth

import java.time.OffsetDateTime

data class LocalLoginAccount(
    val user: AuthUser,
    val identityStatus: AuthIdentityStatus,
    val passwordHash: String,
    val lockedUntil: OffsetDateTime?,
    val roleNames: List<RoleName>,
) {
    fun isLoginAllowedAt(now: OffsetDateTime): Boolean {
        if (user.status != UserStatus.ACTIVE) {
            return false
        }

        if (identityStatus != AuthIdentityStatus.ACTIVE) {
            return false
        }

        return lockedUntil?.isAfter(now) != true
    }
}
