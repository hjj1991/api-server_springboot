package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.AuthEmailMessage

interface SendAuthEmailPort {
    fun send(message: AuthEmailMessage)
}
