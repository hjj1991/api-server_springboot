package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.SocialAuthProviderResult

data class SocialAuthProviderResponse(
    val providerType: String,
    val displayName: String,
    val enabled: Boolean,
    val authorizationPath: String,
) {
    companion object {
        fun from(result: SocialAuthProviderResult): SocialAuthProviderResponse =
            SocialAuthProviderResponse(
                providerType = result.providerType.name,
                displayName = result.displayName,
                enabled = result.enabled,
                authorizationPath = result.authorizationPath,
            )
    }
}
