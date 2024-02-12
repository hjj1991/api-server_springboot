package com.hjj.apiserver.adapter.`in`.web.user.response

import com.hjj.apiserver.domain.user.User

data class UserSignInResponse(
    val nickName: String? = null,
    val picture: String? = null,
    val accessToken: String,
    val refreshToken: String,
) {

    companion object {
        fun fromUserAndToken(user: User, accessToken: String, refreshToken: String): UserSignInResponse {
            return UserSignInResponse(
                user.nickName,
                user.picture,
                accessToken,
                refreshToken,
            )
        }
    }
}