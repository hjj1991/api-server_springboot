package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.ResolveSocialLoginCommand
import com.hjj.apiserver.domain.auth.AuthProviderType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialResolveLoginRequest(
    @field:NotNull
    val providerType: AuthProviderType?,
    @field:NotBlank
    val providerSubject: String?,
    val displayName: String?,
    val email: String?,
    val emailVerified: Boolean = false,
) {
    fun toCommand(): ResolveSocialLoginCommand =
        ResolveSocialLoginCommand(
            providerType = requireNotNull(providerType),
            providerSubject = requireNotNull(providerSubject),
            displayName = displayName,
            email = email,
            emailVerified = emailVerified,
        )
}
