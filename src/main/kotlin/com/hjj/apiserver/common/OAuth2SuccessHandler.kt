package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.dto.oauth2.OAuth2Attribute
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.service.UserService
import com.hjj.apiserver.util.ApiUtils
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2SuccessHandler(
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val oAuth2Attribute = objectMapper.convertValue(oAuth2User.attributes, OAuth2Attribute::class.java)

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
        }


        redirectStrategy.sendRedirect(request, response, objectMapper.writeValueAsString(ApiUtils.success(userSignInResponse)))
    }
}