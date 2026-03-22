package com.hjj.apiserver.adapter.out.security.auth

import com.hjj.apiserver.application.port.out.auth.IssueAccessTokenPort
import com.hjj.apiserver.application.port.out.auth.model.AccessTokenIssueCommand
import com.hjj.apiserver.application.port.out.auth.model.IssuedAccessToken
import com.hjj.apiserver.application.service.auth.AuthProperties
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.util.UUID

@Component
class JwtAccessTokenIssuerAdapter(
    private val jwtEncoder: JwtEncoder,
    private val authProperties: AuthProperties,
) : IssueAccessTokenPort {
    override fun issue(command: AccessTokenIssueCommand): IssuedAccessToken {
        val accessJti = UUID.randomUUID().toString()
        val accessExpiresAt = command.issuedAt.plus(authProperties.accessTokenTtl)
        val tokenValue =
            jwtEncoder.encode(
                JwtEncoderParameters.from(
                    JwtClaimsSet.builder()
                        .subject(command.userId.toString())
                        .issuedAt(command.issuedAt.toInstant())
                        .expiresAt(accessExpiresAt.toInstant())
                        .id(accessJti)
                        .claim("ver", command.tokenVersion)
                        .claim("sid", command.sessionId)
                        .claim("roles", command.roles.distinct().sorted())
                        .build(),
                ),
            ).tokenValue

        return IssuedAccessToken(
            token = tokenValue,
            jti = accessJti,
            issuedAt = command.issuedAt.withOffsetSameInstant(ZoneOffset.UTC),
            expiresAt = accessExpiresAt.withOffsetSameInstant(ZoneOffset.UTC),
        )
    }
}
