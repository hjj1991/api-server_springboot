package com.hjj.apiserver.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.service.UserService
import com.hjj.apiserver.dto.oauth2.OAuth2UserAttribute
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.ws.transport.http.HttpTransportConstants

@Component
class OAuth2SuccessHandler(
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    @Value("\${front.redirect-uri.host}") private val redirectHost: String,
    @Value("\${front.redirect-uri.path.signin}") private val redirectPathSignIn: String,
    @Value("\${front.redirect-uri.path.mapping}") private val redirectPathMapping: String,
    @Value("\${front.redirect-uri.port}") private val redirectPort: String,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2UserAttribute
        val uriComponentsBuilder =
            UriComponentsBuilder.newInstance().scheme(HttpTransportConstants.HTTP_URI_SCHEME).host(redirectHost)
                .port(redirectPort)
//        if(isUserModify(oAuth2User)){
//            val redirectUrl = uriComponentsBuilder
//                .path(redirectPathMapping)
//                .queryParam("provider", oAuth2User.attributes["provider"])
//                .build()
//                .toUriString()
//
//            redirectStrategy.sendRedirect(request, response, redirectUrl)
//        }

//        if (oAuth2User.attributes.containsKey("mappingUserNo")) {
//            userService.socialMapping(oAuth2User)
//            val redirectUrl =
//                uriComponentsBuilder.path(redirectPathMapping).queryParam("provider", oAuth2User.registrationId).build()
//                    .toUriString()
//
//            redirectStrategy.sendRedirect(request, response, redirectUrl)
//        }
//
//        val userAttribute = oAuth2User.toUserAttribute()
//
//        val userSignInResponse = kotlin.runCatching {
//            userService.signUp(userAttribute)
//            userService.signIn(userAttribute)
//        }.recoverCatching { exception ->
//            when (exception::class) {
//                AlreadyExistedUserException::class -> {
//                    userService.signIn(userAttribute)
//                }
//
//                else -> throw Exception()
//            }
//        }.getOrThrow()
//
//
//        val redirectUrl =
//            uriComponentsBuilder.path(redirectPathSignIn).queryParam("accessToken", userSignInResponse.accessToken)
//                .queryParam("refreshToken", userSignInResponse.refreshToken).build().toUriString()
        val redirectUrl = "abc"

        redirectStrategy.sendRedirect(request, response, redirectUrl)
    }
}