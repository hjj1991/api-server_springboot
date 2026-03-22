package com.hjj.apiserver.adapter.out.security.auth

import com.hjj.apiserver.application.port.out.auth.LoadRegisteredSocialProviderPort
import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component

@Component
class RegisteredSocialProviderAdapter(
    private val clientRegistrationRepository: ClientRegistrationRepository,
) : LoadRegisteredSocialProviderPort {
    override fun isEnabled(providerType: AuthProviderType): Boolean {
        if (providerType == AuthProviderType.LOCAL) {
            return false
        }

        val registration = clientRegistrationRepository.findByRegistrationId(providerType.name.lowercase()) ?: return false
        val clientId = registration.clientId?.trim().orEmpty()
        return clientId.isNotBlank() && !clientId.equals("None", ignoreCase = true)
    }
}
