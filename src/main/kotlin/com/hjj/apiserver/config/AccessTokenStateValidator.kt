package com.hjj.apiserver.config

import com.hjj.apiserver.application.port.out.auth.AccessTokenDenylistPort
import com.hjj.apiserver.application.port.out.auth.GetCurrentUserTokenVersionPort
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class AccessTokenStateValidator(
    private val getCurrentUserTokenVersionPort: GetCurrentUserTokenVersionPort,
    private val accessTokenDenylistPort: AccessTokenDenylistPort,
) : OAuth2TokenValidator<Jwt> {
    override fun validate(token: Jwt): OAuth2TokenValidatorResult {
        val userId =
            token.subject?.toLongOrNull()
                ?: return failure("sub claim is required and must be numeric.")

        val tokenVersion =
            when (val claim = token.claims[TOKEN_VERSION_CLAIM]) {
                is Int -> claim.toLong()
                is Long -> claim
                is Number -> claim.toLong()
                is String -> claim.toLongOrNull()
                else -> null
            } ?: return failure("ver claim is required.")

        val jti = token.id ?: return failure("jti claim is required.")

        if (accessTokenDenylistPort.isDenied(jti)) {
            return failure("Access token is denied.")
        }

        val currentVersion =
            getCurrentUserTokenVersionPort.getCurrentTokenVersion(userId)
                ?: return failure("User token version not found.")

        return if (tokenVersion == currentVersion) {
            OAuth2TokenValidatorResult.success()
        } else {
            failure("Access token version is stale.")
        }
    }

    private fun failure(description: String): OAuth2TokenValidatorResult =
        OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", description, null))

    companion object {
        const val TOKEN_VERSION_CLAIM = "ver"
    }
}
