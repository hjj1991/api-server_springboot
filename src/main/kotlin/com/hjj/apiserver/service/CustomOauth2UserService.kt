package com.hjj.apiserver.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOauth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2UserService = DefaultOAuth2UserService()
        val oAuth2User = oAuth2UserService.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

    }
}