package com.hjj.apiserver.common.exception.auth

import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException

class InvalidRefreshSessionException : BaseException(ErrConst.ERR_CODE0018, ErrConst.ERR_CODE0018.msg)
