package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.GetSocialAuthProvidersUseCase
import com.hjj.apiserver.application.port.input.auth.SocialAuthProviderResult
import com.hjj.apiserver.application.port.out.auth.LoadRegisteredSocialProviderPort
import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.stereotype.Service

@Service
class SocialAuthProviderService(
    private val loadRegisteredSocialProviderPort: LoadRegisteredSocialProviderPort,
) : GetSocialAuthProvidersUseCase {
    override fun getAvailableProviders(): List<SocialAuthProviderResult> =
        listOf(
            SocialAuthProviderResult(
                providerType = AuthProviderType.GOOGLE,
                displayName = "Google",
                enabled = loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.GOOGLE),
                authorizationPath = "/oauth2/authorization/google",
            ),
            SocialAuthProviderResult(
                providerType = AuthProviderType.KAKAO,
                displayName = "Kakao",
                enabled = loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.KAKAO),
                authorizationPath = "/oauth2/authorization/kakao",
            ),
            SocialAuthProviderResult(
                providerType = AuthProviderType.NAVER,
                displayName = "Naver",
                enabled = loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.NAVER),
                authorizationPath = "/oauth2/authorization/naver",
            ),
            SocialAuthProviderResult(
                providerType = AuthProviderType.APPLE,
                displayName = "Apple",
                enabled = loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.APPLE),
                authorizationPath = "/oauth2/authorization/apple",
            ),
        )
}
