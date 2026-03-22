package com.hjj.apiserver.adapter.out.security.auth.social

interface SocialProviderProfileExtractor {
    fun supports(registrationId: String): Boolean

    fun extract(attributes: Map<String, Any>): SocialProviderProfile
}
