package com.hjj.apiserver.domain.user

class Credential(
    credentialNo: Long = 0L,
    userId: String,
    credentialEmail: String? = null,
    provider: Provider,
    user: User,
    state: CredentialState,
) {
    var credentialNo: Long = credentialNo
        private set
    var userId: String = userId
        private set
    var credentialEmail: String? = credentialEmail
        private set
    var provider: Provider = provider
        private set
    var user: User = user
        private set

    var state: CredentialState = state
        private set
}
