package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.stereotype.Component

@Component
class KakaoSocialProviderProfileExtractor : SocialProviderProfileExtractor {
    override fun supports(registrationId: String): Boolean = registrationId.equals("kakao", ignoreCase = true)

    @Suppress("UNCHECKED_CAST")
    override fun extract(attributes: Map<String, Any>): SocialProviderProfile {
        val account = attributes["kakao_account"] as? Map<String, Any>
        val profile = account?.get("profile") as? Map<String, Any>
        return SocialProviderProfile(
            providerType = AuthProviderType.KAKAO,
            providerSubject = attributes["id"]?.toString().orEmpty(),
            displayName = profile?.get("nickname")?.toString(),
            email = account?.get("email")?.toString(),
            emailVerified = account?.get("is_email_verified") as? Boolean ?: false,
        )
    }
}
