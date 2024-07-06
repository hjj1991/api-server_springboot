package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.application.port.out.user.ReadUserTokenPort
import com.hjj.apiserver.application.port.out.user.WriteUserTokenPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.common.exception.TokenNotFoundException
import org.springframework.cache.CacheManager

@PersistenceAdapter
class UserTokenPersistenceAdapter(
    private val cacheManager: CacheManager,
) : WriteUserTokenPort, ReadUserTokenPort {
    override fun registerUserToken(
        userNo: Long,
        refreshToken: String,
    ) {
        val cache = cacheManager.getCache("refreshTokenCache")!!
        cache.put(userNo, refreshToken)
    }

    override fun getUserToken(userNo: Long): String {
        val cache =
            cacheManager
                .getCache("refreshTokenCache")!!
                .get(userNo)
                ?: throw TokenNotFoundException("[getUserToken] 해당 사용자의 refreshToken이 존재하지 않습니다.")
        return cache.get() as String
    }

    override fun deleteUserToken(userNo: Long): Boolean {
        val cache = cacheManager.getCache("refreshTokenCache")!!
        return cache.evictIfPresent(userNo)
    }
}
