package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>, UserRepositoryCustom {
//    fun findByUserId(userId: String): UserEntity?
//    fun findByRefreshToken(refreshToken: String): UserEntity?
//    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserEntity?
    fun findByNickName(nickName: String): UserEntity?
    fun findByUsername(username: String): UserEntity?
}
