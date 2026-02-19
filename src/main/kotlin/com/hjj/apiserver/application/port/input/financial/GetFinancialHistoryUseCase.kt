package com.hjj.apiserver.application.port.input.financial

import com.hjj.apiserver.dto.financial.FinancialProductHistorySliceDto
import com.hjj.apiserver.dto.financial.FinancialProductRateHistoryDto

interface GetFinancialHistoryUseCase {
    fun getFinancialProductHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto

    fun getFinancialProductRateHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): List<FinancialProductRateHistoryDto>

    fun getFinancialProductHistories(
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto
}
