package com.hjj.apiserver.application.port.input.auth

import com.hjj.apiserver.domain.auth.AuthProviderType

interface GetSocialAuthProvidersUseCase {
    fun getAvailableProviders(): List<SocialAuthProviderResult>
}

data class SocialAuthProviderResult(
    val providerType: AuthProviderType,
    val displayName: String,
    val enabled: Boolean,
    val authorizationPath: String,
)
