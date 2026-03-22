package com.hjj.apiserver.application.port.out.auth

import java.time.Instant

interface AccessTokenDenylistPort {
    fun isDenied(jti: String): Boolean

    fun deny(
        jti: String,
        expiresAt: Instant,
    )
}
