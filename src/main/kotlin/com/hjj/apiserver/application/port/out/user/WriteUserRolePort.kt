package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.UserRole

interface WriteUserRolePort {

    fun insertUserRole(userRole: UserRole): UserRole
}
