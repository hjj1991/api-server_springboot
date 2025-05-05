package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.Role

interface GetUserRolePort {

    fun findRolesByUserId(userId: Long): Set<Role>
}
