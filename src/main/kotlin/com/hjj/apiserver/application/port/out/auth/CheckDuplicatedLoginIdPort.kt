package com.hjj.apiserver.application.port.out.auth

interface CheckDuplicatedLoginIdPort {
    fun existsByLoginId(loginId: String): Boolean
}
