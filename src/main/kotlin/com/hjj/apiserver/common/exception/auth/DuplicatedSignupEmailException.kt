package com.hjj.apiserver.common.exception.auth

import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException

class DuplicatedSignupEmailException : BaseException(
    errorCode = ErrConst.ERR_CODE0006,
    errorMessage = ErrConst.ERR_CODE0006.msg,
)
