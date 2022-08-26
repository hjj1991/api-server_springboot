package com.hjj.apiserver.common

class ApiError(
    private val errCode: ErrCode,
) {
    val message: String = errCode.msg

}