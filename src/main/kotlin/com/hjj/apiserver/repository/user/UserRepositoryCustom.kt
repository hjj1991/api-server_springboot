package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.User

interface UserRepositoryCustom {
    fun findUserLeftJoinUserLogByUserNo(userNo: Long): User?
}