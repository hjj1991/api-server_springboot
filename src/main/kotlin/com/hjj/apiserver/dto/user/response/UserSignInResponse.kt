package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import java.time.LocalDateTime

data class UserSignInResponse(
    val userId: String? = null,
    val nickName: String,
    val userEmail: String? = null,
    val picture: String? = null,
    val provider: Provider? = null,
    val accessToken: String,
    val refreshToken: String,
    val createdDate: LocalDateTime? = null,
    val lastLoginDateTime: LocalDateTime? = null,
) {

    companion object{
        fun of(user: User, accessToken: String, refreshToken: String): UserSignInResponse{
            return UserSignInResponse(
                user.userId,
                user.nickName,
                user.userEmail,
                user.picture,
                user.provider,
                accessToken,
                refreshToken,
                user.createdAt,
                LocalDateTime.now()
            )
        }
    }
}