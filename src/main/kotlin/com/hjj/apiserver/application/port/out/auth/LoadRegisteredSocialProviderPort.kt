package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.AuthProviderType

interface LoadRegisteredSocialProviderPort {
    fun isEnabled(providerType: AuthProviderType): Boolean
}
