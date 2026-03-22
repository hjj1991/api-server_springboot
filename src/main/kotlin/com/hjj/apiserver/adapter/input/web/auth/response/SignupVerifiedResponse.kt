package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.SignupVerifiedResult
import java.time.OffsetDateTime

data class SignupVerifiedResponse(
    val email: String,
    val verifiedAt: OffsetDateTime,
) {
    companion object {
        fun from(result: SignupVerifiedResult): SignupVerifiedResponse =
            SignupVerifiedResponse(
                email = result.email,
                verifiedAt = result.verifiedAt,
            )
    }
}
