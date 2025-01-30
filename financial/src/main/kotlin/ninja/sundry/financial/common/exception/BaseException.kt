package com.hjj.apiserver.common.exception

import com.hjj.apiserver.common.ErrConst

open class BaseException(
    val errorCode: ErrConst,
    errorMessage: String,
) : RuntimeException(errorMessage)
