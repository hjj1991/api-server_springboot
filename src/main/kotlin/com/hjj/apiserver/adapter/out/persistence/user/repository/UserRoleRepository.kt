package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserRoleEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRoleEntity, Long> {

    @EntityGraph(attributePaths = ["roleEntity"])
    fun findByUserEntityId(userId: Long): List<UserRoleEntity>
}
