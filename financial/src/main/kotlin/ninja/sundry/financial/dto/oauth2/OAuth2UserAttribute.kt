package com.hjj.apiserver.dto.oauth2

import com.hjj.apiserver.common.exception.ProviderNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

class OAuth2UserAttribute(
    val registrationId: String? = null,
    attributes: Map<String, Any>,
    nameAttributeKey: String,
) : DefaultOAuth2User(
        listOf(SimpleGrantedAuthority(Role.USER.key)),
        attributes,
        nameAttributeKey,
    ) {
    fun toUserAttribute(): UserAttribute {
        when (registrationId) {
            "naver" -> {
                val attribute = attributes[name] as LinkedHashMap<String, String>
                return UserAttribute(
                    provider = Provider.NAVER,
                    providerId = attribute["id"] ?: throw IllegalStateException(),
                    userEmail = attribute["email"],
                    nickName = attribute["nickname"] ?: throw IllegalArgumentException(),
                    picture = attribute["profile_image"],
                )
            }

            "kakao" -> {
                val kakaoAccount = attributes["kakao_account"] as LinkedHashMap<String, Any>
                val profile = kakaoAccount["profile"] as LinkedHashMap<String, Any>
                return UserAttribute(
                    provider = Provider.KAKAO,
                    providerId = name,
                    userEmail = kakaoAccount["email"] as String,
                    nickName = profile["nickname"] as String,
                    picture = profile["profile_image_url"] as String,
                )
            }

            else -> throw ProviderNotFoundException()
        }
    }
}
