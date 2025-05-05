package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, String> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.userId = :userId AND r.revoked = false")
    fun revokeAllTokensByUserId(userId: Long): Int

}
