package com.hjj.apiserver.application.port.out.financial

import com.hjj.apiserver.dto.financial.FinancialProductChangeSummaryDto
import com.hjj.apiserver.dto.financial.FinancialRateSeriesPointDto
import java.time.OffsetDateTime

interface GetFinancialAnalyticsPort {
    fun findRateSeries(
        from: OffsetDateTime,
        to: OffsetDateTime,
        bucket: String,
        financialProductId: Long?,
        limit: Int,
    ): List<FinancialRateSeriesPointDto>

    fun findProductChanges(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<FinancialProductChangeSummaryDto>
}
