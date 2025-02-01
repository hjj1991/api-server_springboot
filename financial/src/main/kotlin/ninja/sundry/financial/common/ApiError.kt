package com.hjj.apiserver.common

import ninja.sundry.financial.common.ErrConst

data class ApiError(
    val errCode: ErrConst,
    val message: String = errCode.msg,
    val data: Any? = null,
)
