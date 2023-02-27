package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
): AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {

        if(request.getHeader("Accept") == "application/json"){
            response.status = 401
            response.contentType = "application/json;charset=utf-8"
            response.writer.print(objectMapper.writeValueAsString(ApiError(ErrCode.ERR_CODE0009)))
        }else{
            response.sendError(401)
        }





    }
}