package com.hjj.apiserver.common.exception;

import com.hjj.apiserver.common.ApiError_Java;

public class AlreadyExistedUserException extends Exception{
    public AlreadyExistedUserException () {
        super(String.valueOf(ApiError_Java.ErrCode.ERR_CODE0006));
    }
}
