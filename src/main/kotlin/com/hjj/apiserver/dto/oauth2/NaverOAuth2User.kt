package com.hjj.apiserver.dto.oauth2

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class NaverOAuth2User(
    provider: Provider?,
    providerId: String?,
    picture: String?,
    userPw: String?,
    nickName: String?,
    userId: String?,
    userEmail: String?
) : OAuth2User, UserAttribute() {

    init {

    }


    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }
}