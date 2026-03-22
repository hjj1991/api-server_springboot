package com.hjj.apiserver.application.port.out.auth.model

import java.time.OffsetDateTime

data class CreateLocalUserAccountCommand(
    val displayName: String,
    val loginId: String,
    val encryptedEmail: String,
    val emailLookupHash: String,
    val emailVerifiedAt: OffsetDateTime,
    val providerSubjectHash: String,
    val passwordHash: String,
    val passwordAlgo: String,
    val passwordUpdatedAt: OffsetDateTime,
)
