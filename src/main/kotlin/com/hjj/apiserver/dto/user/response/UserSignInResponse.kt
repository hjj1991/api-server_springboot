package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.Provider
import java.time.LocalDateTime

class UserSignInResponse(
    val userId: String?,
    val nickName: String,
    val userEmail: String,
    val picture: String?,
    val provider: Provider?,
    val accessToken: String,
    val refreshToken: String,
    val createdDate: LocalDateTime,
    val lastLoginDateTime: LocalDateTime,
    val expireTime: LocalDateTime,
) {
}