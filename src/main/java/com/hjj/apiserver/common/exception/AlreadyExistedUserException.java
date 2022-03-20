package com.hjj.apiserver.common.exception;

import com.hjj.apiserver.common.ApiError;

public class AlreadyExistedUserException extends Exception{
    public AlreadyExistedUserException () {
        super(String.valueOf(ApiError.ErrCode.ERR_CODE0006));
    }
}
