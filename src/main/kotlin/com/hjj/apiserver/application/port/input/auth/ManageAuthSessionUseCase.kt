package com.hjj.apiserver.application.port.input.auth

import java.time.OffsetDateTime

interface ManageAuthSessionUseCase {
    fun login(command: LocalLoginCommand): AuthSessionResult

    fun refresh(command: RefreshSessionCommand): AuthSessionResult

    fun logout(command: LogoutSessionCommand)

    fun logoutAll(command: LogoutAllSessionsCommand)

    fun getCurrentUser(query: GetCurrentUserQuery): AuthenticatedUserResult
}

data class LocalLoginCommand(
    val loginId: String,
    val password: String,
)

data class RefreshSessionCommand(
    val refreshToken: String,
)

data class LogoutSessionCommand(
    val userId: Long,
    val accessJti: String,
    val accessExpiresAt: OffsetDateTime,
    val refreshToken: String? = null,
)

data class LogoutAllSessionsCommand(
    val userId: Long,
    val accessJti: String,
    val accessExpiresAt: OffsetDateTime,
)

data class GetCurrentUserQuery(
    val userId: Long,
)

data class AuthSessionResult(
    val accessToken: String,
    val accessTokenExpiresAt: OffsetDateTime,
    val refreshToken: String,
    val refreshTokenExpiresAt: OffsetDateTime,
)

data class AuthenticatedUserResult(
    val id: Long,
    val displayName: String,
    val email: String?,
    val roles: List<String>,
)
