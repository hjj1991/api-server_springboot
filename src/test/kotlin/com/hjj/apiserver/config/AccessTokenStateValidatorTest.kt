package com.hjj.apiserver.config

import com.hjj.apiserver.application.port.out.auth.AccessTokenDenylistPort
import com.hjj.apiserver.application.port.out.auth.GetCurrentUserTokenVersionPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class AccessTokenStateValidatorTest {
    @Test
    fun `jti 가 denylist 에 있지 않고 token version 이 일치하면 성공한다`() {
        val validator =
            AccessTokenStateValidator(
                getCurrentUserTokenVersionPort =
                    object : GetCurrentUserTokenVersionPort {
                        override fun getCurrentTokenVersion(userId: Long): Long? = 4L
                    },
                accessTokenDenylistPort =
                    object : AccessTokenDenylistPort {
                        override fun isDenied(jti: String): Boolean = false

                        override fun deny(
                            jti: String,
                            expiresAt: Instant,
                        ) = Unit
                    },
            )

        val result = validator.validate(validJwt(version = 4L, jti = "allowed-jti"))

        assertThat(result.hasErrors()).isFalse()
    }

    @Test
    fun `token version 이 다르면 실패한다`() {
        val validator =
            AccessTokenStateValidator(
                getCurrentUserTokenVersionPort =
                    object : GetCurrentUserTokenVersionPort {
                        override fun getCurrentTokenVersion(userId: Long): Long? = 4L
                    },
                accessTokenDenylistPort =
                    object : AccessTokenDenylistPort {
                        override fun isDenied(jti: String): Boolean = false

                        override fun deny(
                            jti: String,
                            expiresAt: Instant,
                        ) = Unit
                    },
            )

        val result = validator.validate(validJwt(version = 3L, jti = "allowed-jti"))

        assertThat(result.hasErrors()).isTrue()
        assertThat(result.errors.first().description).contains("version")
    }

    @Test
    fun `jti 가 denylist 에 있으면 실패한다`() {
        val validator =
            AccessTokenStateValidator(
                getCurrentUserTokenVersionPort =
                    object : GetCurrentUserTokenVersionPort {
                        override fun getCurrentTokenVersion(userId: Long): Long? = 4L
                    },
                accessTokenDenylistPort =
                    object : AccessTokenDenylistPort {
                        override fun isDenied(jti: String): Boolean = true

                        override fun deny(
                            jti: String,
                            expiresAt: Instant,
                        ) = Unit
                    },
            )

        val result = validator.validate(validJwt(version = 4L, jti = "denied-jti"))

        assertThat(result.hasErrors()).isTrue()
        assertThat(result.errors.first().description).contains("denied")
    }

    private fun validJwt(
        version: Long,
        jti: String,
    ): Jwt =
        Jwt.withTokenValue("access-token")
            .header("alg", "HS256")
            .subject("100")
            .claim("jti", jti)
            .claim(AccessTokenStateValidator.TOKEN_VERSION_CLAIM, version)
            .issuedAt(Instant.parse("2026-03-22T00:00:00Z"))
            .expiresAt(Instant.parse("2026-03-22T00:05:00Z"))
            .build()
}
