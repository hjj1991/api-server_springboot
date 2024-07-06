package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.QCredentialEntity.Companion.credentialEntity
import com.hjj.apiserver.domain.user.Provider
import com.querydsl.jpa.impl.JPAQueryFactory

class CredentialRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CredentialRepositoryCustom {
    override fun findExistsUserIdByUserIdAndProvider(
        userId: String,
        provider: Provider,
    ): Boolean {
        return jpaQueryFactory.selectOne()
            .from(credentialEntity)
            .where(credentialEntity.userId.eq(userId), credentialEntity.provider.eq(provider)).fetchOne() != null
    }
}
