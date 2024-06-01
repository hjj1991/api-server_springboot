package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.user.Provider
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CredentialRepository : JpaRepository<CredentialEntity, Long>, CredentialRepositoryCustom {
    @EntityGraph(attributePaths = ["userEntity"])
    fun findCredentialEntityByUserIdAndProvider(
        userId: String,
        provider: Provider,
    ): CredentialEntity?
}
