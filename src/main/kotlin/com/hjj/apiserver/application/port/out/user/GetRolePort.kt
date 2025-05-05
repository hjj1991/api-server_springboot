package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.RoleType

interface GetRolePort {

    fun findByRoleType(roleType: RoleType): Role
}
