package com.hjj.apiserver.application.port.out.financial

import com.hjj.apiserver.dto.financial.FinancialProductHistorySliceDto
import com.hjj.apiserver.dto.financial.FinancialProductRateHistoryDto

interface GetFinancialHistoryPort {
    fun findFinancialProductHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto

    fun findFinancialProductRateHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): List<FinancialProductRateHistoryDto>

    fun findFinancialProductHistories(
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto
}
