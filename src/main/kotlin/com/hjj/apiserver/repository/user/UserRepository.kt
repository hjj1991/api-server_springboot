package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>, UserRepositoryCustom {
    fun findByUserId(userId: String): User?
    fun findByRefreshToken(refreshToken: String): User?
    fun findByProviderAndProviderId(provider: Provider, providerId: String): User?
    fun findByNickName(nickName: String): User?
    fun existsByProviderIdAndProviderAndDeleteYn(providerId: String, provider: Provider, deletedYn: Char = 'N'): Boolean
}