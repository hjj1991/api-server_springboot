package com.hjj.apiserver.common

import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.AlreadyExistsUserException
import com.hjj.apiserver.common.exception.DuplicatedNickNameException
import com.hjj.apiserver.common.exception.DuplicatedUserIdException
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import jakarta.servlet.http.HttpServletRequest
import mu.two.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors

@RestControllerAdvice
class ExceptionControllerAdvice {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleUserNotFoundException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0001)
    }

    @ExceptionHandler(DuplicatedNickNameException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun hanldeDuplicatedNickNameException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0003)
    }

    @ExceptionHandler(DuplicatedUserIdException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun handleDuplicatedNickNameException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0002)
    }

    // 소셜 로그인 계정이 있는데 일반계정 로그인 시도시
    @ExceptionHandler(ExistedSocialUserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun existedSocialUserException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0007)
    }

    // 패스워드가 일치하지 않은 경우
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun badCredentialsException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0008)
    }

    @ExceptionHandler(AlreadyExistsUserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun alreadyExistedUserException(
        request: HttpServletRequest,
        e: Exception,
    ): ApiError {
        log.error(e.message)
        return ApiError(ErrConst.ERR_CODE0006)
    }

    @ExceptionHandler(AccountBookNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun accountBookNotFoundException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0010)
    }

    @ExceptionHandler(FinancialProductNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected fun financialProductNotFoundException(request: HttpServletRequest): ApiError {
        return ApiError(ErrConst.ERR_CODE0012)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun methodArgumentNotValidException(
        request: HttpServletRequest,
        e: Exception,
    ): ApiError {
        log.error(e.message)
        return ApiError(ErrConst.ERR_CODE9999)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected fun basicException(
        request: HttpServletRequest,
        e: Exception,
    ): ApiError {
        log.error("[{}] Error Request: {}, ErrorInfo: {}", e.javaClass.name, request, e.printStackTrace())
        return ApiError(ErrConst.ERR_CODE9999)
    }

    fun makeValidFailMessage(bindingResult: BindingResult): String {
        return bindingResult.fieldErrors.stream().map { fieldError: FieldError -> fieldError.defaultMessage }
            .collect(Collectors.joining(","))
    }
}
