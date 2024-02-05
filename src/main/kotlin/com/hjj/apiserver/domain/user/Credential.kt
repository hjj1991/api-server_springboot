package com.hjj.apiserver.domain.user

class Credential(
    val credentialNo: Long = 0L,
    val userId: String,
    val credentialEmail: String? = null,
    val provider: Provider,
    val user: User,
) {
}