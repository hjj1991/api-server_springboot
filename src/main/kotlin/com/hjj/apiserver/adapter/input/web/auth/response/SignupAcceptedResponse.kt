package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.SignupAcceptedResult
import java.time.OffsetDateTime

data class SignupAcceptedResponse(
    val email: String,
    val expiresAt: OffsetDateTime,
) {
    companion object {
        fun from(result: SignupAcceptedResult): SignupAcceptedResponse =
            SignupAcceptedResponse(
                email = result.email,
                expiresAt = result.expiresAt,
            )
    }
}
