package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.User

interface WriteUserPort {
    fun registerUser(user: User): User
}