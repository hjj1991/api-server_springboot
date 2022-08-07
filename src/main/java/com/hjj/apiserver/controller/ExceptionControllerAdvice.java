package com.hjj.apiserver.controller;


import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.common.exception.AlreadyExistedUserException;
import com.hjj.apiserver.common.exception.ExistedSocialUserException;
import com.hjj.apiserver.common.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse userNotFoundException(HttpServletRequest request, UserNotFoundException e) {
        return ApiUtils.error(ApiError.ErrCode.ERR_CODE0001.getMsg(), ApiError.ErrCode.ERR_CODE0001);
    }

    @ExceptionHandler(AlreadyExistedUserException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse alreadyExistedUserException(HttpServletRequest request, AlreadyExistedUserException e){
        return ApiUtils.error(ApiError.ErrCode.ERR_CODE0006.getMsg(), ApiError.ErrCode.ERR_CODE0006);
    }

    /* 소셜 로그인 계정이 있는데 일반계정 로그인 시도시 */
    @ExceptionHandler(ExistedSocialUserException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse existedSocialUserException(HttpServletRequest request, ExistedSocialUserException e){
        return ApiUtils.error(ApiError.ErrCode.ERR_CODE0007.getMsg(), ApiError.ErrCode.ERR_CODE0007);
    }

    /* 패스워드가 일치하지 않은 경우 */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse badCredentialsException(HttpServletRequest request, BadCredentialsException e){
        return ApiUtils.error(e.getMessage(), ApiError.ErrCode.ERR_CODE9999);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e){
        log.warn("Valid anotation 에러 통과하지 못함 url {}", request.getRequestURI());

        return ApiUtils.error(makeValidFailMessage(e.getBindingResult()), ApiError.ErrCode.ERR_CODE9999);

    }


    public String makeValidFailMessage(BindingResult bindingResult){

        return bindingResult.getFieldErrors().stream().map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(","));

    }
}
