package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.stereotype.Component

@Component
class GoogleSocialProviderProfileExtractor : SocialProviderProfileExtractor {
    override fun supports(registrationId: String): Boolean = registrationId.equals("google", ignoreCase = true)

    override fun extract(attributes: Map<String, Any>): SocialProviderProfile =
        SocialProviderProfile(
            providerType = AuthProviderType.GOOGLE,
            providerSubject = attributes["sub"]?.toString().orEmpty(),
            displayName = attributes["name"]?.toString(),
            email = attributes["email"]?.toString(),
            emailVerified = attributes["email_verified"] as? Boolean ?: false,
        )
}
