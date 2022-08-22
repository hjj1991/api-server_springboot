package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.repositroy.UserRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>, UserRepositoryCustom {
    fun existsUserByUserId(userId: String):Boolean
    fun findByUserId(userId: String): User?
    fun findByRefreshToken(refreshToken: String): User?
}