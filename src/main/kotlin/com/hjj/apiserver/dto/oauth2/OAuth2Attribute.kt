package com.hjj.apiserver.dto.oauth2

import com.hjj.apiserver.common.exception.ProviderNotFoundException
import com.hjj.apiserver.domain.user.Provider
import org.springframework.security.oauth2.core.user.OAuth2User

class OAuth2Attribute(
    val provider: Provider,
    val providerId: String,
    val userEmail: String? = null,
    val nickName: String,
    val name: String? = null,
    val picture: String? = null,
) {

    companion object {

        fun of(
            registrationId: String?,
            oAuth2User: OAuth2User,
            userNameAttributeName: String
        ): OAuth2Attribute {
            when (registrationId) {
                "naver" -> {
                    val attribute = oAuth2User.attributes[userNameAttributeName] as LinkedHashMap<String, String>
                    return OAuth2Attribute(
                        provider = Provider.NAVER,
                        providerId = attribute["id"] ?: throw IllegalStateException(),
                        userEmail = attribute["email"],
                        nickName = attribute["nickname"] ?: throw IllegalArgumentException(),
                        name = attribute["name"],
                        picture = attribute["profile_image"]
                    )
                }

                "kakao" -> {
                    val kakaoAccount = oAuth2User.attributes["kakao_account"] as LinkedHashMap<String, Any>
                    val profile = kakaoAccount["profile"] as LinkedHashMap<String, Any>
                    return OAuth2Attribute(
                        provider = Provider.KAKAO,
                        providerId = userNameAttributeName,
                        userEmail = kakaoAccount["email"] as String,
                        nickName = profile["nickname"] as String,
                        name = kakaoAccount["name"] as String,
                        picture = profile["profile_image_url"] as String
                    )
                }

                else -> throw ProviderNotFoundException()
            }
        }
    }


}