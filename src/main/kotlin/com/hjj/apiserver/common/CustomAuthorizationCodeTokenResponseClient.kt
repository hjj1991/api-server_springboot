package com.hjj.apiserver.common

import org.springframework.core.convert.converter.Converter
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

class CustomAuthorizationCodeTokenResponseClient : OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private var requestEntityConverter: Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<*>> =
        OAuth2AuthorizationCodeGrantRequestEntityConverter()
    private var restOperations: RestOperations

    init {
        val restTemplate = RestTemplate(
            listOf(FormHttpMessageConverter(), OAuth2AccessTokenResponseHttpMessageConverter())
        )
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()
        restOperations = restTemplate
    }

    override fun getTokenResponse(
        authorizationCodeGrantRequest: OAuth2AuthorizationCodeGrantRequest
    ): OAuth2AccessTokenResponse {
        Assert.notNull(authorizationCodeGrantRequest, "authorizationCodeGrantRequest cannot be null")
        val request = requestEntityConverter.convert(authorizationCodeGrantRequest)
        val response = getResponse(request)
        var tokenResponse = response.body
        if (CollectionUtils.isEmpty(tokenResponse.accessToken.scopes)) {
            tokenResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse)
                .scopes(authorizationCodeGrantRequest.clientRegistration.scopes)
                .additionalParameters(authorizationCodeGrantRequest.authorizationExchange.authorizationRequest.additionalParameters)
                .build()
        }
        return tokenResponse
    }

    private fun getResponse(request: RequestEntity<*>): ResponseEntity<OAuth2AccessTokenResponse> {
        try {
            return restOperations.exchange(request, OAuth2AccessTokenResponse::class.java)
        } catch (ex: RestClientException) {
            val oauth2Error = OAuth2Error(
                INVALID_TOKEN_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: ${ex.message}",
                null
            )
            throw OAuth2AuthorizationException(oauth2Error, ex)
        }
    }


    fun setRequestEntityConverter(
        requestEntityConverter: Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<*>>
    ) {
        Assert.notNull(requestEntityConverter, "requestEntityConverter cannot be null")
        this.requestEntityConverter = requestEntityConverter
    }


    fun setRestOperations(restOperations: RestOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null")
        this.restOperations = restOperations
    }

    companion object {
        private val INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response"
    }
}
