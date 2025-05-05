package com.hjj.apiserver.dto.oauth2

import com.hjj.apiserver.domain.user.RoleType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

class OAuth2UserAttribute(
    val registrationId: String? = null,
    attributes: Map<String, Any>,
    nameAttributeKey: String,
) : DefaultOAuth2User(
        listOf(SimpleGrantedAuthority(RoleType.USER.code)),
        attributes,
        nameAttributeKey,
    ) {
}
