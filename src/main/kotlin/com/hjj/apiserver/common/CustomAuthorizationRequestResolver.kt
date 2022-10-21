package com.hjj.apiserver.common

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import javax.servlet.http.HttpServletRequest

class CustomAuthorizationRequestResolver(
    clientRegistrationRepository: ClientRegistrationRepository,
): OAuth2AuthorizationRequestResolver {
    private val defaultAuthorizationRequestResolver: OAuth2AuthorizationRequestResolver =
        DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, "/oauth2/authorization"
        )

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val authorizationRequest = this.defaultAuthorizationRequestResolver.resolve(request)
        return if(authorizationRequest != null){
            customAuthorizationRequest(authorizationRequest, request)
        }else{
            null
        }
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        val authorizationRequest = this.defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId)
        return if(authorizationRequest != null){
            customAuthorizationRequest(authorizationRequest, request)
        }else{
            null
        }
    }

    private fun customAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest, request: HttpServletRequest): OAuth2AuthorizationRequest{
        val additionalParameters = LinkedHashMap<String, Any>(authorizationRequest.additionalParameters)
        request.parameterNames.toList().filter { param -> param == "accessToken" || param == "modify" }
            .forEach { param -> additionalParameters[param] = request.getParameter(param) }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(additionalParameters)
            .build()
    }
}