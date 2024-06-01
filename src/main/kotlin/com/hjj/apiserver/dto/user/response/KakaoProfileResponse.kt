package com.hjj.apiserver.dto.user.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoProfileResponse(
    val id: String,
    val properties: Properties,
    val kakaoAccount: KakaoAccount,
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    class Properties(
        val nickname: String,
        val thumbnailImage: String? = null,
        val profileImage: String? = null,
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    class KakaoAccount(
        val profile: Profile,
        val hasEmail: Boolean? = null,
        val email: String? = null,
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    class Profile(
        val nickname: String,
        val profileImageUrl: String? = null,
    )
}
