package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.AuthSessionResult
import java.time.OffsetDateTime

data class AuthSessionResponse(
    val accessToken: String,
    val accessTokenExpiresAt: OffsetDateTime,
    val refreshToken: String,
    val refreshTokenExpiresAt: OffsetDateTime,
) {
    companion object {
        fun from(result: AuthSessionResult): AuthSessionResponse =
            AuthSessionResponse(
                accessToken = result.accessToken,
                accessTokenExpiresAt = result.accessTokenExpiresAt,
                refreshToken = result.refreshToken,
                refreshTokenExpiresAt = result.refreshTokenExpiresAt,
            )
    }
}
