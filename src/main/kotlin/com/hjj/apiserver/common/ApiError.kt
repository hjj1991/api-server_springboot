package com.hjj.apiserver.common

class ApiError(
    private val errCode: ErrCode,
) {
    private val message: String = errCode.msg

}