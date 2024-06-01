package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        if (request.getHeader("Accept") == "application/json") {
            response.status = 401
            response.contentType = "application/json;charset=utf-8"
            response.writer.print(objectMapper.writeValueAsString(ApiError(ErrConst.ERR_CODE0009)))
        } else {
            response.sendError(401)
        }
    }
}
