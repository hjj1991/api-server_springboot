package com.hjj.apiserver.dto.user

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.user.Provider
import org.springframework.security.crypto.password.PasswordEncoder

open class UserAttribute(
    val provider: Provider? = null,
    val providerId: String? = null,
    val picture: String? = null,
    val userPw: String? = null,
    val nickName: String? = null,
    val userId: String? = null,
    val userEmail: String? = null,
) {

    fun toUserEntity(changeNickName: String?=null): UserEntity {
        return UserEntity(
//            userId = userId!!,
            nickName = changeNickName ?: nickName!!,
            userEmail = userEmail,
            picture = picture,
        )
    }

    fun toEncryptPwUserEntity(passwordEncoder: PasswordEncoder): UserEntity {
        return UserEntity(
//            userId = userId!!,
            nickName = nickName!!,
            userEmail = userEmail,
            userPw = passwordEncoder.encode(userPw),
            picture = picture,
        )
    }

    companion object {

        fun ofNaver(attribute: LinkedHashMap<String, String>): UserAttribute {
            return UserAttribute(
                provider = Provider.NAVER,
                providerId = attribute["id"] ?: throw IllegalStateException(),
                userEmail = attribute["email"],
                nickName = attribute["nickname"] ?: throw IllegalArgumentException(),
                picture = attribute["profile_image"]
            )
        }

        fun ofKakao(attribute: LinkedHashMap<String, Any>): UserAttribute {
            val profile = attribute["profile"] as LinkedHashMap<String, Any>
            return UserAttribute(
                provider = Provider.KAKAO,
                providerId = "userNameAttributeName",
                userEmail = attribute["email"] as String,
                nickName = profile["nickname"] as String,
                picture = profile["profile_image_url"] as String
            )
        }

        fun ofGeneral(userId: String, userPw: String): UserAttribute {
            return UserAttribute(
                userId = userId,
                userPw = userPw,
            )
        }
    }
}