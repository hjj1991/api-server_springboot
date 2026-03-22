package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.domain.auth.RoleName
import com.hjj.apiserver.adapter.out.persistence.auth.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findByRoleName(roleName: RoleName): RoleEntity?
}
