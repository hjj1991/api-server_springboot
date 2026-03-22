package com.hjj.apiserver.application.port.out.auth.model

import com.hjj.apiserver.domain.auth.AuthProviderType
import java.time.OffsetDateTime

data class LinkSocialIdentityAccountCommand(
    val userId: Long,
    val providerType: AuthProviderType,
    val providerSubjectHash: String,
    val linkedAt: OffsetDateTime,
)
