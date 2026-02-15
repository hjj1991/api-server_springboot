package com.hjj.apiserver.common

import com.hjj.apiserver.common.exception.NotFoundException
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import mu.two.KotlinLogging
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice(
    private val apiProblemFactory: ApiProblemFactory,
) {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(BadCredentialsException::class)
    protected fun handleBadCredentialsException(request: HttpServletRequest): ProblemDetail {
        return apiProblemFactory.create(ErrConst.ERR_CODE0008, request)
    }

    @ExceptionHandler(FinancialProductNotFoundException::class)
    protected fun handleFinancialProductNotFoundException(request: HttpServletRequest): ProblemDetail {
        return apiProblemFactory.create(ErrConst.ERR_CODE0014, request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ProblemDetail {
        log.error(e.message)
        val violations =
            e.bindingResult.fieldErrors.map {
                ValidationErrorDetail(
                    field = it.field,
                    message = it.defaultMessage ?: ErrConst.ERR_CODE0016.msg,
                    rejectedValue = it.rejectedValue,
                )
            }
        return apiProblemFactory.create(
            errCode = ErrConst.ERR_CODE0016,
            request = request,
            detail = ErrConst.ERR_CODE0016.msg,
            details = violations,
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    protected fun handleConstraintViolationException(
        e: ConstraintViolationException,
        request: HttpServletRequest,
    ): ProblemDetail {
        return apiProblemFactory.create(ErrConst.ERR_CODE0016, request, e.message ?: ErrConst.ERR_CODE0016.msg)
    }

    @ExceptionHandler(NotFoundException::class)
    protected fun handleNotFoundException(
        exception: NotFoundException,
        request: HttpServletRequest,
    ): ProblemDetail {
        return apiProblemFactory.create(errCode = exception.errorConst, request = request)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleGenericException(
        e: Exception,
        request: HttpServletRequest,
    ): ProblemDetail {
        log.error(e.message, e)
        return apiProblemFactory.create(ErrConst.ERR_CODE9999, request)
    }
}
