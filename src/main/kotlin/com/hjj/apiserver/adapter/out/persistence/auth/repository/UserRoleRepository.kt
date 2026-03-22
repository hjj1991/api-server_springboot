package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.adapter.out.persistence.auth.entity.UserRoleEntity
import com.hjj.apiserver.domain.auth.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRoleRepository : JpaRepository<UserRoleEntity, Long> {
    @Query("select ur.roleEntity.roleName from UserRoleEntity ur where ur.userEntity.id = :userId")
    fun findRoleNamesByUserId(
        @Param("userId") userId: Long,
    ): List<RoleName>
}
