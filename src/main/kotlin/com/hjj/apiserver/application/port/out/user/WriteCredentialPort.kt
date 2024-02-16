package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.Credential

interface WriteCredentialPort {
    fun registerCredential(credential: Credential): Credential
}
