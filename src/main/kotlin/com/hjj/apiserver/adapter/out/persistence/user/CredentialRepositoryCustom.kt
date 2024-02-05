package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.user.Provider

interface CredentialRepositoryCustom {
    fun findCredentialUserIdByUserIdAndProvider(userId: String, provider: Provider): Boolean
}