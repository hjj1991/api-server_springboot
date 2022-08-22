package com.hjj.apiserver.dto.user.response

import com.fasterxml.jackson.annotation.JsonProperty

class KakaoProfileResponse {

    val id: String? = null
    val properties: Properties? = null

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount? = null


    class Properties {
        val nickname: String? = null
        val thumbnail_image: String? = null
        val profile_image: String? = null
    }


    class KakaoAccount {
        val profile: Profile? = null

        @JsonProperty("has_email")
        val hasEmail: Boolean? = null
        val email: String? = null
    }

    class Profile {
        val nickname: String? = null

        @JsonProperty("profile_image_url")
        val profileImageUrl: String? = null
    }
}