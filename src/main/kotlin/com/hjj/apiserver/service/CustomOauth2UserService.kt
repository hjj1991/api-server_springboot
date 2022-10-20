package com.hjj.apiserver.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hjj.apiserver.dto.oauth2.OAuth2Attribute
import org.modelmapper.ModelMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOauth2UserService(
    private val objectMapper: ObjectMapper,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2UserService = DefaultOAuth2UserService()
        val oAuth2User = oAuth2UserService.loadUser(userRequest)
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName


        val registrationId = userRequest.clientRegistration.registrationId

        val oAuth2Attribute = OAuth2Attribute.of(registrationId, oAuth2User, userNameAttributeName)

        val mutableMap = objectMapper.convertValue(oAuth2Attribute, Map::class.java)

        return DefaultOAuth2User(
            listOf(SimpleGrantedAuthority("ROLE_USER")),
            mutableMap as MutableMap<String, Any>,
            "providerId"
        )

    }


}