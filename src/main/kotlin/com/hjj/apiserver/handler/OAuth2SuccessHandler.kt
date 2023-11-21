package com.hjj.apiserver.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.dto.oauth2.OAuth2Attribute
import com.hjj.apiserver.service.impl.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.ws.transport.http.HttpTransportConstants
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class OAuth2SuccessHandler(
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    @Value("\${front.redirect-uri.host}")
    private val redirectHost: String,
    @Value("\${front.redirect-uri.path.signin}")
    private val redirectPathSignIn: String,
    @Value("\${front.redirect-uri.path.mapping}")
    private val redirectPathMapping: String,
    @Value("\${front.redirect-uri.port}")
    private val redirectPort: String,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val uriComponentsBuilder = UriComponentsBuilder.newInstance()
            .scheme(HttpTransportConstants.HTTP_URI_SCHEME)
            .host(redirectHost)
            .port(redirectPort)

        if(isUserModify(oAuth2User)){
            val redirectUrl = uriComponentsBuilder
                .path(redirectPathMapping)
                .queryParam("provider", oAuth2User.attributes["provider"])
                .build()
                .toUriString()

            redirectStrategy.sendRedirect(request, response, redirectUrl)
        }

        val oAuth2Attribute = objectMapper.convertValue(oAuth2User.attributes, OAuth2Attribute::class.java)

        if (oAuth2User.attributes.containsKey("mappingUserNo")) {
            userService.socialMapping(oAuth2User)
            val redirectUrl = uriComponentsBuilder
                .path(redirectPathMapping)
                .queryParam("provider", oAuth2Attribute.provider)
                .build()
                .toUriString()

            redirectStrategy.sendRedirect(request, response, redirectUrl)
        }

        val userSignInResponse = kotlin.runCatching {
            userService.signUp(oAuth2Attribute)
            userService.signIn(oAuth2Attribute)
        }.recoverCatching { exception ->
            when (exception::class) {
                AlreadyExistedUserException::class -> {
                    userService.socialSignIn(oAuth2Attribute)
                }

                else -> throw Exception()
            }
        }.getOrThrow()



        val redirectUrl = uriComponentsBuilder
            .path(redirectPathSignIn)
            .queryParam("accessToken", userSignInResponse.accessToken)
            .queryParam("refreshToken", userSignInResponse.refreshToken)
            .build()
            .toUriString()


        redirectStrategy.sendRedirect(request, response, redirectUrl)
    }

    private fun isUserModify(oAuth2User: OAuth2User): Boolean{
        return oAuth2User.attributes.getOrDefault("modify", false) as Boolean
    }
}