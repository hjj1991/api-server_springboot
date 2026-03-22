package com.hjj.apiserver.application.port.out.auth

interface IncreaseStoredUserTokenVersionPort {
    fun increaseTokenVersion(userId: Long): Long
}
