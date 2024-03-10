package com.hjj.apiserver.common

data class ApiError(
    val errCode: ErrConst,
    val message: String = errCode.msg,
    val data: Any? = null,
)
