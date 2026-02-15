package com.hjj.apiserver.common

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
    private val apiProblemFactory: ApiProblemFactory,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val acceptHeader = request.getHeader(ErrorResponseConstants.ACCEPT_HEADER)
        if (acceptHeader?.contains(ErrorResponseConstants.JSON_MEDIA_TYPE, ignoreCase = true) == true) {
            val problem = apiProblemFactory.create(ErrConst.ERR_CODE0009, request)
            response.status = problem.status
            response.contentType = ErrorResponseConstants.PROBLEM_JSON_CONTENT_TYPE
            response.writer.print(objectMapper.writeValueAsString(problem))
        } else {
            response.sendError(ErrConst.ERR_CODE0009.status.value())
        }
    }
}
