package com.hjj.apiserver.application.port.out.auth

interface LoadStoredUserTokenVersionPort {
    fun loadTokenVersion(userId: Long): Long?
}
