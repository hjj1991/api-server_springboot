package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.util.ApiUtils
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
            response.writer.print(objectMapper.writeValueAsString(ApiUtils.error(ErrCode.ERR_CODE0009)))
        }

        response.sendError(401)



    }
}