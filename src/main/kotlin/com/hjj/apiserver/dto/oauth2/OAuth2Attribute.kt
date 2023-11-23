package com.hjj.apiserver.dto.oauth2

import com.hjj.apiserver.common.exception.ProviderNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User

class OAuth2Attribute(registrationId: String?, oAuth2User: DefaultOAuth2User, nameAttributeKey: String) : OAuth2User {
    val oAuth2User: DefaultOAuth2User
    val userAttribute: UserAttribute

    init {
        this.oAuth2User = oAuth2User
        when (registrationId) {
            "naver" -> {
                val attribute = oAuth2User.attributes[nameAttributeKey] as LinkedHashMap<String, String>
                userAttribute = UserAttribute(
                    provider = Provider.NAVER,
                    providerId = attribute ["id"] ?: throw IllegalStateException(),
                    userEmail = attribute["email"],
                    nickName = attribute["nickname"] ?: throw IllegalArgumentException(),
                    picture = attribute["profile_image"]
                )
            }

            "kakao" -> {
                val kakaoAccount = oAuth2User.attributes["kakao_account"] as LinkedHashMap<String, Any>
                val profile = kakaoAccount["profile"] as LinkedHashMap<String, Any>
                userAttribute = UserAttribute(
                    provider = Provider.KAKAO,
                    providerId = nameAttributeKey,
                    userEmail = kakaoAccount["email"] as String,
                    nickName = profile["nickname"] as String,
                    picture = profile["profile_image_url"] as String
                )
            }

            else -> throw ProviderNotFoundException()
        }
    }


    override fun getName(): String {
        return oAuth2User.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return oAuth2User.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return oAuth2User.authorities
    }


}