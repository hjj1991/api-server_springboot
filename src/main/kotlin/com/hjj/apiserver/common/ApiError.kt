package com.hjj.apiserver.common

data class ApiError(
    val errCode: ErrCode,
    val message: String = errCode.msg
) {
}