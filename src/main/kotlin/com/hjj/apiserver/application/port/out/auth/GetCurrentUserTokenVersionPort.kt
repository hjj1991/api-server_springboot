package com.hjj.apiserver.application.port.out.auth

interface GetCurrentUserTokenVersionPort {
    fun getCurrentTokenVersion(userId: Long): Long?
}
