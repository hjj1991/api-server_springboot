package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.AuthProviderType
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount

interface LoadSocialAuthenticatedUserPort {
    fun loadByProviderSubjectHash(
        providerType: AuthProviderType,
        providerSubjectHash: String,
    ): AuthenticatedUserAccount?
}
