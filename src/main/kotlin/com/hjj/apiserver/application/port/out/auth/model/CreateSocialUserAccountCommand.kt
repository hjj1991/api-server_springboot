package com.hjj.apiserver.application.port.out.auth.model

import com.hjj.apiserver.domain.auth.AuthProviderType
import java.time.OffsetDateTime

data class CreateSocialUserAccountCommand(
    val providerType: AuthProviderType,
    val providerSubjectHash: String,
    val displayName: String,
    val encryptedEmail: String?,
    val emailLookupHash: String?,
    val emailVerifiedAt: OffsetDateTime?,
    val linkedAt: OffsetDateTime,
)
