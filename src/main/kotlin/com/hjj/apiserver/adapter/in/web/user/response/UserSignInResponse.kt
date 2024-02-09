package com.hjj.apiserver.adapter.`in`.web.user.response

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class UserSignInResponse(
    val userId: String? = null,
    val nickName: String? = null,
    val userEmail: String? = null,
    val picture: String? = null,
    val accessToken: String,
    val refreshToken: String,
    val createdDate: ZonedDateTime? = null,
    val lastLoginDateTime: LocalDateTime? = null,
) {

    companion object{
        fun of(userEntity: UserEntity, accessToken: String, refreshToken: String): UserSignInResponse {
            return UserSignInResponse(
                "userEntity.userId",
                userEntity.nickName,
                userEntity.userEmail,
                userEntity.picture,
                accessToken,
                refreshToken,
                userEntity.createdAt,
                LocalDateTime.now()
            )
        }
    }
}