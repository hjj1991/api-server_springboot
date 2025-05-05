package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.User

interface GetUserPort {
    fun existsUserNickName(nickName: String): Boolean

    fun findById(userId: Long): User

    fun findByUsername(username: String): User
}
