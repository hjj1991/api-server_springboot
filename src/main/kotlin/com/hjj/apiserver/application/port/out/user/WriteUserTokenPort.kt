package com.hjj.apiserver.application.port.out.user

interface WriteUserTokenPort {
    fun registerUserToken(userNo: Long, refreshToken: String)
    fun deleteUserToken(userNo: Long): Boolean
}