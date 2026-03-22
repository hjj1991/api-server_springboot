package com.hjj.apiserver.common.exception.auth

import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException

class AlreadyLinkedSocialIdentityException : BaseException(
    errorCode = ErrConst.ERR_CODE0006,
    errorMessage = "이미 다른 계정에 연결된 간편 로그인입니다.",
)
