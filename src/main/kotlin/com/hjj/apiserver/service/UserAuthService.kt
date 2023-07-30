package com.hjj.apiserver.service

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.security.oauth2.core.user.OAuth2User

interface UserAuthService {
    fun register(userAttribute: UserAttribute): User
    fun signIn(userAttribute: UserAttribute): User
    fun isMatchingProvider(provider: Provider?): Boolean
    fun socialMapping(oAuth2User: OAuth2User) {

    }
}