package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductHistorySliceResponse
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductRateHistoryResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialHistoryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/financial-products")
class FinancialHistoryController(
    private val getFinancialHistoryUseCase: GetFinancialHistoryUseCase,
) {
    @GetMapping("/{financialProductId}/histories", headers = [ApiVersionConstants.HEADER_V1])
    fun listFinancialProductHistoriesByProductId(
        @PathVariable financialProductId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = DEFAULT_HISTORY_LIMIT) limit: Int,
    ): FinancialProductHistorySliceResponse {
        val historySlice = getFinancialHistoryUseCase.getFinancialProductHistoriesByProductId(financialProductId, cursor, limit.coerceIn(1, 200))
        return FinancialProductHistorySliceResponse.from(historySlice)
    }

    @GetMapping("/{financialProductId}/rate-histories", headers = [ApiVersionConstants.HEADER_V1])
    fun listFinancialProductRateHistoriesByProductId(
        @PathVariable financialProductId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = DEFAULT_HISTORY_LIMIT) limit: Int,
    ): List<FinancialProductRateHistoryResponse> {
        val rateHistories = getFinancialHistoryUseCase.getFinancialProductRateHistoriesByProductId(financialProductId, cursor, limit.coerceIn(1, 200))
        return rateHistories.map(FinancialProductRateHistoryResponse::from)
    }

    private companion object {
        const val DEFAULT_HISTORY_LIMIT = "20"
    }
}
