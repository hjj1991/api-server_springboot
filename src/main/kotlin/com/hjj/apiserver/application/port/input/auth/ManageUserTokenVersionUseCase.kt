package com.hjj.apiserver.application.port.input.auth

interface ManageUserTokenVersionUseCase {
    fun increaseTokenVersion(userId: Long): Long

    fun evictTokenVersion(userId: Long)
}
