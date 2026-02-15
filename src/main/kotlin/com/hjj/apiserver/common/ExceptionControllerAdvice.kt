package com.hjj.apiserver.common

import com.hjj.apiserver.common.exception.NotFoundException
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import jakarta.validation.ConstraintViolationException
import mu.two.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleBadCredentialsException(): ApiError {
        return ApiError(ErrConst.ERR_CODE0008)
    }

    @ExceptionHandler(FinancialProductNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected fun handleFinancialProductNotFoundException(): ApiError {
        return ApiError(ErrConst.ERR_CODE0012)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ApiError {
        log.error(e.message)
        return ApiError(ErrConst.ERR_CODE9999)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleConstraintViolationException(e: ConstraintViolationException): ApiError {
        return ApiError(ErrConst.ERR_CODE0016, e.message ?: ErrConst.ERR_CODE0016.msg)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected fun handleNotFoundException(exception: NotFoundException): ApiError {
        return ApiError(errCode = exception.errorConst)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected fun handleGenericException(e: Exception): ApiError {
        log.error(e.message, e)
        return ApiError(ErrConst.ERR_CODE9999)
    }

    fun makeValidFailMessage(bindingResult: BindingResult): String {
        return bindingResult.fieldErrors.joinToString(",") { it.defaultMessage ?: "" }
    }
}
