package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.adapter.out.persistence.auth.entity.AuthIdentityEntity
import com.hjj.apiserver.domain.auth.AuthProviderType
import org.springframework.data.jpa.repository.JpaRepository

interface AuthIdentityRepository : JpaRepository<AuthIdentityEntity, Long> {
    fun findByUserEntityIdAndProviderType(
        userId: Long,
        providerType: AuthProviderType,
    ): AuthIdentityEntity?

    fun findByProviderTypeAndLoginId(
        providerType: AuthProviderType,
        loginId: String,
    ): AuthIdentityEntity?

    fun existsByProviderTypeAndLoginId(
        providerType: AuthProviderType,
        loginId: String,
    ): Boolean

    fun findByProviderTypeAndProviderSubjectHash(
        providerType: AuthProviderType,
        providerSubjectHash: String,
    ): AuthIdentityEntity?

    fun findAllByUserEntityIdOrderByLinkedAtAsc(userId: Long): List<AuthIdentityEntity>
}
