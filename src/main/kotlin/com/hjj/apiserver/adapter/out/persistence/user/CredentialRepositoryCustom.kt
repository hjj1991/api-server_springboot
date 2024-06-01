package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.user.Provider

interface CredentialRepositoryCustom {
    fun findExistsUserIdByUserIdAndProvider(
        userId: String,
        provider: Provider,
    ): Boolean
}
