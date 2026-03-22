package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.stereotype.Component

@Component
class AppleSocialProviderProfileExtractor : SocialProviderProfileExtractor {
    override fun supports(registrationId: String): Boolean = registrationId.equals("apple", ignoreCase = true)

    override fun extract(attributes: Map<String, Any>): SocialProviderProfile =
        SocialProviderProfile(
            providerType = AuthProviderType.APPLE,
            providerSubject = attributes["sub"]?.toString().orEmpty(),
            displayName = attributes["name"]?.toString(),
            email = attributes["email"]?.toString(),
            emailVerified = !attributes["email"]?.toString().isNullOrBlank(),
        )
}
