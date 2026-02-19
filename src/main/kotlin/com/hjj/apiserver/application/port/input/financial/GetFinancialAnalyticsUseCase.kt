package com.hjj.apiserver.application.port.input.financial

import com.hjj.apiserver.dto.financial.FinancialProductChangeSummaryDto
import com.hjj.apiserver.dto.financial.FinancialRateSeriesPointDto
import java.time.OffsetDateTime

interface GetFinancialAnalyticsUseCase {
    fun getRateSeries(
        from: OffsetDateTime,
        to: OffsetDateTime,
        bucket: String,
        financialProductId: Long?,
        limit: Int,
    ): List<FinancialRateSeriesPointDto>

    fun getProductChanges(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<FinancialProductChangeSummaryDto>
}
