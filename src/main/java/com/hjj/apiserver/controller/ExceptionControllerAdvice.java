package com.hjj.apiserver.controller;


import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ApiResponse userNotFoundException(HttpServletRequest request, UsernameNotFoundException e) {
        return ApiUtils.error(ApiError.ErrCode.ERR_CODE0001.getMsg(), ApiError.ErrCode.ERR_CODE0001);
    }
}
