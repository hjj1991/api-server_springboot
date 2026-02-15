package com.hjj.apiserver.common

import com.hjj.apiserver.config.ErrorResponseProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import java.net.URI

@Component
class ApiProblemFactory(
    private val errorResponseProperties: ErrorResponseProperties,
) {
    fun create(
        errCode: ErrConst,
        request: HttpServletRequest,
        detail: String = errCode.msg,
        details: Any? = null,
    ): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(errCode.status, detail)
        problem.title = errCode.status.reasonPhrase
        val baseUri = errorResponseProperties.problemTypeBaseUri.trimEnd('/')
        problem.type = URI.create("$baseUri/${errCode.name}")
        problem.instance = URI.create(request.requestURI)
        problem.setProperty(ErrorResponseConstants.CODE_PROPERTY, errCode.name)
        if (details != null) {
            problem.setProperty(ErrorResponseConstants.DETAILS_PROPERTY, details)
        }
        return problem
    }
}

data class ValidationErrorDetail(
    val field: String,
    val message: String,
    val rejectedValue: Any?,
)
