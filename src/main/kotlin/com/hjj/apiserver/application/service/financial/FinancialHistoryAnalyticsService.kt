package com.hjj.apiserver.application.service.financial

import com.hjj.apiserver.application.port.input.financial.GetFinancialAnalyticsUseCase
import com.hjj.apiserver.application.port.input.financial.GetFinancialHistoryUseCase
import com.hjj.apiserver.application.port.out.financial.GetFinancialAnalyticsPort
import com.hjj.apiserver.application.port.out.financial.GetFinancialHistoryPort
import com.hjj.apiserver.dto.financial.FinancialProductChangeSummaryDto
import com.hjj.apiserver.dto.financial.FinancialProductHistorySliceDto
import com.hjj.apiserver.dto.financial.FinancialProductRateHistoryDto
import com.hjj.apiserver.dto.financial.FinancialRateSeriesPointDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class FinancialHistoryAnalyticsService(
    private val getFinancialHistoryPort: GetFinancialHistoryPort,
    private val getFinancialAnalyticsPort: GetFinancialAnalyticsPort,
) : GetFinancialHistoryUseCase, GetFinancialAnalyticsUseCase {
    @Transactional(readOnly = true)
    override fun getFinancialProductHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto {
        return getFinancialHistoryPort.findFinancialProductHistoriesByProductId(financialProductId, cursor, limit)
    }

    @Transactional(readOnly = true)
    override fun getFinancialProductRateHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): List<FinancialProductRateHistoryDto> {
        return getFinancialHistoryPort.findFinancialProductRateHistoriesByProductId(financialProductId, cursor, limit)
    }

    @Transactional(readOnly = true)
    override fun getFinancialProductHistories(
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto {
        return getFinancialHistoryPort.findFinancialProductHistories(cursor, limit)
    }

    @Transactional(readOnly = true)
    override fun getRateSeries(
        from: OffsetDateTime,
        to: OffsetDateTime,
        bucket: String,
        financialProductId: Long?,
        limit: Int,
    ): List<FinancialRateSeriesPointDto> {
        return getFinancialAnalyticsPort.findRateSeries(from, to, bucket, financialProductId, limit)
    }

    @Transactional(readOnly = true)
    override fun getProductChanges(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<FinancialProductChangeSummaryDto> {
        return getFinancialAnalyticsPort.findProductChanges(from, to)
    }
}
