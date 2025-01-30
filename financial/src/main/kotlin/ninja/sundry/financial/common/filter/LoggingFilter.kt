package com.hjj.apiserver.common.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.two.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.StringUtils
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

private val log = KotlinLogging.logger {}

@Component
class LoggingFilter : Filter {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain,
    ) {
        val contentCachingRequestWrapper = ContentCachingRequestWrapper(request as HttpServletRequest)
        val contentCachingResponseWrapper = ContentCachingResponseWrapper(response as HttpServletResponse)
        logRequest(contentCachingRequestWrapper)
        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper)
        logResponse(contentCachingRequestWrapper, contentCachingResponseWrapper)
    }

    companion object {
        fun logRequest(request: ContentCachingRequestWrapper) {
            val method = request.method
            val requestURI = request.requestURI
            val queryString = request.queryString
            val formattedQueryString = if (StringUtils.hasText(queryString)) "?$queryString" else ""
            val requestBody = getRequestBody(request)
            if (requestBody.isEmpty()) {
                log.info { "RCV | $method $requestURI$formattedQueryString" }
            } else {
                log.info { "RCV | $method $requestURI$formattedQueryString | body = $requestBody" }
            }
        }

        fun logResponse(
            request: ContentCachingRequestWrapper,
            response: ContentCachingResponseWrapper,
        ) {
            val method = request.method
            val requestURI = request.requestURI
            val queryString = request.queryString
            val formattedQueryString = if (StringUtils.hasText(queryString)) "?$queryString" else ""
            val status = response.status
            val responseBody = getResponseBody(response)
            val logMessage =
                if (responseBody.isEmpty()) {
                    "SNT | $method $requestURI$formattedQueryString | $status"
                } else {
                    "SNT | $method $requestURI$formattedQueryString | $status | body = $responseBody"
                }
            if (status < HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                log.info { logMessage }
            } else {
                log.error { logMessage }
            }
        }

        private fun getRequestBody(request: ContentCachingRequestWrapper): String {
            return kotlin.runCatching {
                request.contentAsString
            }.getOrDefault("")
        }

        private fun getResponseBody(response: ContentCachingResponseWrapper): String {
            val contentInputStream = response.contentInputStream
            return kotlin.runCatching {
                val responseBody = StreamUtils.copyToString(contentInputStream, StandardCharsets.UTF_8)
                response.copyBodyToResponse()
                return responseBody
            }.getOrDefault("")
        }
    }
}
