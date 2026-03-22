package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.PendingSignup

interface PendingSignupPort {
    fun save(
        verificationToken: String,
        signup: PendingSignup,
    )

    fun findByVerificationToken(token: String): PendingSignup?

    fun deleteByVerificationToken(token: String)
}
