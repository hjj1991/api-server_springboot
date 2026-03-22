package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.LocalLoginAccount

interface LoadLocalLoginAccountPort {
    fun loadByLoginId(loginId: String): LocalLoginAccount?
}
