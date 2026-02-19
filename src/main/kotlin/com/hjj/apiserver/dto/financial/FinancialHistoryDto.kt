package com.hjj.apiserver.dto.financial

import java.math.BigDecimal
import java.time.OffsetDateTime

data class FinancialProductHistoryDto(
    val observedAt: OffsetDateTime,
    val financialProductId: Long,
    val financialCompanyId: Long,
    val financialProductCode: String,
    val financialProductType: String,
    val status: String,
    val productContentHash: String?,
    val payload: String,
)

data class FinancialProductHistorySliceDto(
    val items: List<FinancialProductHistoryDto>,
    val nextCursor: String?,
)

data class FinancialProductRateHistoryDto(
    val observedAt: OffsetDateTime,
    val financialProductId: Long,
    val financialProductOptionId: Long?,
    val interestRateType: String,
    val reserveType: String?,
    val depositPeriodMonths: Int,
    val baseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
    val payload: String?,
)

data class FinancialRateSeriesPointDto(
    val bucket: OffsetDateTime,
    val averageBaseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
)

data class FinancialProductChangeSummaryDto(
    val status: String,
    val count: Long,
)
