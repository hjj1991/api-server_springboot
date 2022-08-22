package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.Provider
import java.time.LocalDateTime

class UserSignInResponse(
    val userId: String? = null,
    val nickName: String,
    val userEmail: String,
    val picture: String? = null,
    val provider: Provider? = null,
    val accessToken: String,
    val refreshToken: String,
    val createdDate: LocalDateTime? = null,
    val lastLoginDateTime: LocalDateTime? = null,
) {
}