package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.adapter.out.persistence.auth.entity.LocalCredentialEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LocalCredentialRepository : JpaRepository<LocalCredentialEntity, Long> {
    fun findByUserEntityId(userId: Long): LocalCredentialEntity?
}
