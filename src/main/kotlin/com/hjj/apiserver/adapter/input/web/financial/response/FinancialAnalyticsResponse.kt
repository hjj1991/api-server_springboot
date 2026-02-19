package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.dto.financial.FinancialProductChangeSummaryDto
import com.hjj.apiserver.dto.financial.FinancialRateSeriesPointDto
import java.math.BigDecimal
import java.time.OffsetDateTime

data class FinancialRateSeriesPointResponse(
    val bucket: OffsetDateTime,
    val averageBaseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
) {
    companion object {
        fun from(dto: FinancialRateSeriesPointDto): FinancialRateSeriesPointResponse =
            FinancialRateSeriesPointResponse(
                bucket = dto.bucket,
                averageBaseInterestRate = dto.averageBaseInterestRate,
                maximumInterestRate = dto.maximumInterestRate,
            )
    }
}

data class FinancialProductChangeSummaryResponse(
    val status: String,
    val count: Long,
) {
    companion object {
        fun from(dto: FinancialProductChangeSummaryDto): FinancialProductChangeSummaryResponse =
            FinancialProductChangeSummaryResponse(
                status = dto.status,
                count = dto.count,
            )
    }
}
