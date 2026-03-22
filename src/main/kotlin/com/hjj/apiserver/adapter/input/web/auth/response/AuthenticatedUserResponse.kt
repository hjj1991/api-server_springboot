package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.AuthenticatedUserResult

data class AuthenticatedUserResponse(
    val id: Long,
    val displayName: String,
    val email: String?,
    val roles: List<String>,
) {
    companion object {
        fun from(result: AuthenticatedUserResult): AuthenticatedUserResponse =
            AuthenticatedUserResponse(
                id = result.id,
                displayName = result.displayName,
                email = result.email,
                roles = result.roles,
            )
    }
}
