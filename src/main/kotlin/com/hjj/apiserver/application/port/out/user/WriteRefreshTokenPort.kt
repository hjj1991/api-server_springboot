package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.RefreshToken

interface WriteRefreshTokenPort {
    fun revokeAllTokensByUserId(userId: Long): Int

    fun insertRefreshToken(refreshToken: RefreshToken): RefreshToken
}
