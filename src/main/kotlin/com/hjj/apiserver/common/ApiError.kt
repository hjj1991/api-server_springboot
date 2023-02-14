package com.hjj.apiserver.common

data class ApiError(
    private val errCode: ErrCode,
    private val message: String = errCode.msg
) {
}