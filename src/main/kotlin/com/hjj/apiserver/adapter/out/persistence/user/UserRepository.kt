package com.hjj.apiserver.adapter.out.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, Long>, UserRepositoryCustom {
//    fun findByUserId(userId: String): UserEntity?
//    fun findByRefreshToken(refreshToken: String): UserEntity?
//    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserEntity?
//    fun findByNickName(nickName: String): UserEntity?
//    fun existsByProviderIdAndProviderAndDeleteYn(providerId: String, provider: Provider, deletedYn: Char = 'N'): Boolean
}