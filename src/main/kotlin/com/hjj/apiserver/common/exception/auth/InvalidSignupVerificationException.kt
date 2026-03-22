package com.hjj.apiserver.common.exception.auth

import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException

class InvalidSignupVerificationException : BaseException(
    errorCode = ErrConst.ERR_CODE0017,
    errorMessage = ErrConst.ERR_CODE0017.msg,
)
