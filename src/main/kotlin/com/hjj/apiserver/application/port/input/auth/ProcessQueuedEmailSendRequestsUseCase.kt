package com.hjj.apiserver.application.port.input.auth

interface ProcessQueuedEmailSendRequestsUseCase {
    fun processNextBatch(): Int
}
