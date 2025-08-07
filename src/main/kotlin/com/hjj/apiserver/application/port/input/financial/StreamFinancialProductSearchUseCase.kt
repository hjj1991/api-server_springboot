package com.hjj.apiserver.application.port.input.financial

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface StreamFinancialProductSearchUseCase {
    fun searchFinancialProduct(query: String): SseEmitter
}
