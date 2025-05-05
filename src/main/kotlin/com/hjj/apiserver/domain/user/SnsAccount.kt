package com.hjj.apiserver.domain.user

data class SnsAccount(
    val id: Long = 0L,
    val snsId: String,
    val snsEmail: String?,
    val provider: Provider,
    val user: User,
    val state: SnsAccountStatusType,
)
