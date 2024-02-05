package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.Provider

interface GetCredentialPort {
    fun findExistsCredentialByUserIdAndProvider(userId: String, provider: Provider): Boolean
}