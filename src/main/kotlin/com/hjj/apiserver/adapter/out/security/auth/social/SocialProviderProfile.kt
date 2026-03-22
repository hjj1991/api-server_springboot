package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType

data class SocialProviderProfile(
    val providerType: AuthProviderType,
    val providerSubject: String,
    val displayName: String?,
    val email: String?,
    val emailVerified: Boolean,
)
