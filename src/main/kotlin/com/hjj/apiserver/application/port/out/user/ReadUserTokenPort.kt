package com.hjj.apiserver.application.port.out.user

interface ReadUserTokenPort {
    fun getUserToken(userNo: Long): String
}