package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductHistorySliceResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialHistoryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/financial-product-histories")
class FinancialProductHistoryController(
    private val getFinancialHistoryUseCase: GetFinancialHistoryUseCase,
) {
    @GetMapping(headers = [ApiVersionConstants.HEADER_V1])
    fun listFinancialProductHistories(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = DEFAULT_HISTORY_LIMIT) limit: Int,
    ): FinancialProductHistorySliceResponse {
        val historySlice = getFinancialHistoryUseCase.getFinancialProductHistories(cursor, limit.coerceIn(1, 200))
        return FinancialProductHistorySliceResponse.from(historySlice)
    }

    private companion object {
        const val DEFAULT_HISTORY_LIMIT = "20"
    }
}
