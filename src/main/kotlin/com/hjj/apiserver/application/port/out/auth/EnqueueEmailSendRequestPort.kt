package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest

interface EnqueueEmailSendRequestPort {
    fun enqueue(request: EmailSendRequest)
}
