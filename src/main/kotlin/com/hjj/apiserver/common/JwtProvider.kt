package com.hjj.apiserver.common

import com.hjj.apiserver.common.enums.JwtTokenType
import com.hjj.apiserver.domain.user.Role
import com.nimbusds.jose.JWSAlgorithm
import mu.two.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class JwtProvider(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String,
) {
    private val log = KotlinLogging.logger {}

    companion object {
        const val CLAIM_TOKEN_TYPE = "token_type"
        const val CLAIM_ROLES = "roles"
        const val ACCESS_TOKEN_VALID_MILLISECONDS: Long = 1000L * 60 * 20
        const val REFRESH_TOKEN_VALID_MILLISECONDS: Long = 1000L * 3600 * 24 * 14 // 2주 동안만 토큰 유효
        const val REFRESH_TOKEN_REISSUED_REQUIRED_MILLISECONDS: Long = 1000L * 3600 * 24 * 7 // 1주이하로 남은 경우 토큰 재발급
    }

    fun createAccessToken(userId: Long, roles: Set<Role>): Jwt {
        val now = Instant.now()
        val exp = createExpiresAt(now, JwtTokenType.ACCESS)
        val jti = UUID.randomUUID().toString()

        val header = JwsHeader.with { JWSAlgorithm.HS256.name }.build()
        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(exp)
            .subject(userId.toString())
            .id(jti)
            .claim(CLAIM_TOKEN_TYPE, JwtTokenType.ACCESS.value)
            .claim(CLAIM_ROLES, roles.map { it.roleType })
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
    }

    fun createRefreshToken(userId: Long): Jwt {
        val now = Instant.now()
        val exp = createExpiresAt(now, JwtTokenType.REFRESH)
        val jti = UUID.randomUUID().toString()

        val header = JwsHeader.with { JWSAlgorithm.HS256.name }.build()
        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(exp)
            .subject(userId.toString())
            .id(jti)
            .claim(CLAIM_TOKEN_TYPE, JwtTokenType.REFRESH.value)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
    }

    private fun createExpiresAt(now: Instant, jwtTokenType: JwtTokenType): Instant =
        when (jwtTokenType) {
            JwtTokenType.ACCESS -> now.plus(ACCESS_TOKEN_VALID_MILLISECONDS, ChronoUnit.MILLIS)
            JwtTokenType.REFRESH -> now.plus(REFRESH_TOKEN_VALID_MILLISECONDS, ChronoUnit.MILLIS)
        }
}

