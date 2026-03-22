package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.ManageUserTokenVersionUseCase
import com.hjj.apiserver.application.port.out.auth.GetCurrentUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.IncreaseStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.LoadStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.UserTokenVersionCachePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserTokenVersionService(
    private val loadStoredUserTokenVersionPort: LoadStoredUserTokenVersionPort,
    private val increaseStoredUserTokenVersionPort: IncreaseStoredUserTokenVersionPort,
    private val userTokenVersionCachePort: UserTokenVersionCachePort,
    private val authProperties: AuthProperties,
) : GetCurrentUserTokenVersionPort,
    ManageUserTokenVersionUseCase {
    override fun getCurrentTokenVersion(userId: Long): Long? {
        userTokenVersionCachePort.get(userId)?.let { return it }

        val currentVersion = loadStoredUserTokenVersionPort.loadTokenVersion(userId) ?: return null
        cacheTokenVersion(userId = userId, tokenVersion = currentVersion)
        return currentVersion
    }

    @Transactional
    override fun increaseTokenVersion(userId: Long): Long {
        val updatedVersion = increaseStoredUserTokenVersionPort.increaseTokenVersion(userId)
        cacheTokenVersion(userId = userId, tokenVersion = updatedVersion)
        return updatedVersion
    }

    fun cacheTokenVersion(
        userId: Long,
        tokenVersion: Long,
    ) {
        userTokenVersionCachePort.set(userId, tokenVersion, authProperties.tokenVersionCacheTtl)
    }

    override fun evictTokenVersion(userId: Long) {
        userTokenVersionCachePort.delete(userId)
    }
}
