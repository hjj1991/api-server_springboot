package com.hjj.apiserver.application.service.financial

import com.hjj.apiserver.application.port.input.financial.StreamFinancialProductSearchUseCase
import com.hjj.apiserver.application.port.out.financial.StreamFinancialProductSearchPort
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class StreamFinancialProductSearchService(
    private val streamFinancialProductSearchPort: StreamFinancialProductSearchPort,
) : StreamFinancialProductSearchUseCase {
    override fun searchFinancialProduct(query: String): SseEmitter {
        return streamFinancialProductSearchPort.searchFinancialProduct(query)
    }
}
