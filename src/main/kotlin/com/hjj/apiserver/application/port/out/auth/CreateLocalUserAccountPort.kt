package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.CreateLocalUserAccountCommand

interface CreateLocalUserAccountPort {
    fun create(command: CreateLocalUserAccountCommand)
}
