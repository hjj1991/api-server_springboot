package com.hjj.apiserver.application.port.input.auth

import java.time.OffsetDateTime

interface RegisterLocalSignupUseCase {
    fun signup(command: LocalSignupCommand): SignupAcceptedResult

    fun verifySignup(command: VerifySignupCommand): SignupVerifiedResult
}

data class LocalSignupCommand(
    val displayName: String,
    val loginId: String,
    val email: String,
    val password: String,
)

data class VerifySignupCommand(
    val token: String,
)

data class SignupAcceptedResult(
    val email: String,
    val expiresAt: OffsetDateTime,
)

data class SignupVerifiedResult(
    val email: String,
    val verifiedAt: OffsetDateTime,
)
