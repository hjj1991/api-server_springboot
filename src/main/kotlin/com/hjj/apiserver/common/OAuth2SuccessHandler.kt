package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.dto.oauth2.OAuth2Attribute
import com.hjj.apiserver.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.ws.transport.http.HttpTransportConstants
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
        val oAuth2Attribute = objectMapper.convertValue(oAuth2User.attributes, OAuth2Attribute::class.java)



        if (oAuth2User.attributes.containsKey("mappingUserNo")) {
            userService.socialMapping(oAuth2User)
            val redirectUrl = UriComponentsBuilder.newInstance()
                .scheme(HttpTransportConstants.HTTP_URI_SCHEME)
                .host(redirectHost)
                .port(redirectPort)
                .path(redirectPathMapping)
                .queryParam("provider", oAuth2Attribute.provider)
                .build()
                .toUriString()

            redirectStrategy.sendRedirect(request, response, redirectUrl)
            return
        }

        val userSignInResponse = kotlin.runCatching {
            userService.socialSignUp(oAuth2Attribute)
            userService.socialSignIn(oAuth2Attribute)
        }.recoverCatching { exception ->
            when (exception::class) {
                AlreadyExistedUserException::class -> {
                    userService.socialSignIn(oAuth2Attribute)
                }

                else -> throw Exception()
            }
        }.getOrThrow()



        val redirectUrl = UriComponentsBuilder.newInstance()
            .scheme(HttpTransportConstants.HTTP_URI_SCHEME)
            .host(redirectHost)
            .port(redirectPort)
            .path(redirectPathSignIn)
            .queryParam("accessToken", userSignInResponse.accessToken)
            .queryParam("refreshToken", userSignInResponse.refreshToken)
            .build()
            .toUriString()


        redirectStrategy.sendRedirect(request, response, redirectUrl)
    }
}