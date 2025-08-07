package com.hjj.apiserver.application.port.out.financial

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface StreamFinancialProductSearchPort {
    fun searchFinancialProduct(query: String): SseEmitter
}
