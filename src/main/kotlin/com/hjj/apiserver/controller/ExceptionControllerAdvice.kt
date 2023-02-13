package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.logger
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors
import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class ExceptionControllerAdvice{
    private val log: Logger = logger()


    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.OK)
    protected fun userNotFoundException(request: HttpServletRequest): ApiResponse<*>{
        return ApiUtils.error(ErrCode.ERR_CODE0001)
    }

    /* 소셜 로그인 계정이 있는데 일반계정 로그인 시도시 */
    @ExceptionHandler(ExistedSocialUserException::class)
    @ResponseStatus(HttpStatus.OK)
    protected fun existedSocialUserException(request: HttpServletRequest): ApiResponse<*>{
        return ApiUtils.error(ErrCode.ERR_CODE0007)
    }

    /* 패스워드가 일치하지 않은 경우 */
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.OK)
    protected fun badCredentialsException(request: HttpServletRequest): ApiResponse<*>{
        return ApiUtils.error(ErrCode.ERR_CODE0008)
    }

    @ExceptionHandler(AlreadyExistedUserException::class)
    @ResponseStatus(HttpStatus.OK)
    protected fun alreadyExistedUserException(request: HttpServletRequest): ApiResponse<*>{
        return ApiUtils.error(ErrCode.ERR_CODE0006)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun methodArgumentNotValidException(request: HttpServletRequest): ApiResponse<*>{
        return ApiUtils.error(ErrCode.ERR_CODE9999)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected fun basicException(request: HttpServletRequest, e: Exception): ApiResponse<*> {
        log.error("[{}] Error Request: {}, ErrorInfo: {}", e.javaClass.name, request, e.printStackTrace())
        return ApiUtils.error(ErrCode.ERR_CODE9999)
    }

    fun makeValidFailMessage(bindingResult: BindingResult): String {
        return bindingResult.fieldErrors.stream().map { fieldError: FieldError -> fieldError.defaultMessage }
            .collect(Collectors.joining(","))
    }

}