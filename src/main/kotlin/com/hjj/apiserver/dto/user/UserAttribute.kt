package com.hjj.apiserver.dto.user

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import org.springframework.security.crypto.password.PasswordEncoder

class UserAttribute(
    val provider: Provider? = null,
    val providerId: String? = null,
    val picture: String? = null,
    val userPw: String? = null,
    val nickName: String,
    val userId: String? = null,
    val userEmail: String? = null,
) {

    fun toUserEntity(changeNickName: String?=null): User{
        return User(
            userId = userId,
            nickName = changeNickName ?: nickName,
            userEmail = userEmail,
            picture = picture,
            provider = provider,
            providerId = providerId,
        )
    }

    fun toEncryptPwUserEntity(passwordEncoder: PasswordEncoder): User{
        return User(
            userId = userId,
            nickName = nickName,
            userEmail = userEmail,
            userPw = passwordEncoder.encode(userPw),
            picture = picture,
            provider = provider,
            providerId = providerId,
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
    }
}