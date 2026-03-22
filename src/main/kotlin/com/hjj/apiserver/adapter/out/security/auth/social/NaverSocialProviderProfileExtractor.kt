package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.stereotype.Component

@Component
class NaverSocialProviderProfileExtractor : SocialProviderProfileExtractor {
    override fun supports(registrationId: String): Boolean = registrationId.equals("naver", ignoreCase = true)

    @Suppress("UNCHECKED_CAST")
    override fun extract(attributes: Map<String, Any>): SocialProviderProfile {
        val response = attributes["response"] as? Map<String, Any> ?: emptyMap()
        val email = response["email"]?.toString()
        return SocialProviderProfile(
            providerType = AuthProviderType.NAVER,
            providerSubject = response["id"]?.toString().orEmpty(),
            displayName = response["name"]?.toString() ?: response["nickname"]?.toString(),
            email = email,
            emailVerified = !email.isNullOrBlank(),
        )
    }
}
