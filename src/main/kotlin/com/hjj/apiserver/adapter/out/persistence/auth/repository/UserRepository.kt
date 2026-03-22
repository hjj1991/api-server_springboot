package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.adapter.out.persistence.auth.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmailLookupHash(emailLookupHash: String): Boolean

    fun findByEmailLookupHash(emailLookupHash: String): UserEntity?

    @Query("select u.tokenVersion from UserEntity u where u.id = :userId")
    fun findTokenVersionById(
        @Param("userId") userId: Long,
    ): Long?
}
