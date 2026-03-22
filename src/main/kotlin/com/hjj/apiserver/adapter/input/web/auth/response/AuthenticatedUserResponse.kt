package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.AuthenticatedUserResult
import java.time.OffsetDateTime

data class AuthenticatedUserResponse(
    val id: Long,
    val displayName: String,
    val email: String?,
    val roles: List<String>,
    val linkedProviders: List<LinkedAuthProviderResponse>,
) {
    companion object {
        fun from(result: AuthenticatedUserResult): AuthenticatedUserResponse =
            AuthenticatedUserResponse(
                id = result.id,
                displayName = result.displayName,
                email = result.email,
                roles = result.roles,
                linkedProviders =
                    result.linkedIdentities.map { identity ->
                        LinkedAuthProviderResponse(
                            providerType = identity.providerType.name,
                            loginId = identity.loginId,
                            linkedAt = identity.linkedAt,
                            lastLoginAt = identity.lastLoginAt,
                        )
                    },
            )
    }
}

data class LinkedAuthProviderResponse(
    val providerType: String,
    val loginId: String?,
    val linkedAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?,
)
