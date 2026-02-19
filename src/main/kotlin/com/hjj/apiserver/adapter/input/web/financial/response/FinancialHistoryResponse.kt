package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.dto.financial.FinancialProductHistoryDto
import com.hjj.apiserver.dto.financial.FinancialProductHistorySliceDto
import com.hjj.apiserver.dto.financial.FinancialProductRateHistoryDto
import java.math.BigDecimal
import java.time.OffsetDateTime

data class FinancialProductHistoryResponse(
    val observedAt: OffsetDateTime,
    val financialProductId: Long,
    val financialCompanyId: Long,
    val financialProductCode: String,
    val financialProductType: String,
    val status: String,
    val productContentHash: String?,
    val payload: String,
) {
    companion object {
        fun from(dto: FinancialProductHistoryDto): FinancialProductHistoryResponse =
            FinancialProductHistoryResponse(
                observedAt = dto.observedAt,
                financialProductId = dto.financialProductId,
                financialCompanyId = dto.financialCompanyId,
                financialProductCode = dto.financialProductCode,
                financialProductType = dto.financialProductType,
                status = dto.status,
                productContentHash = dto.productContentHash,
                payload = dto.payload,
            )
    }
}

data class FinancialProductHistorySliceResponse(
    val items: List<FinancialProductHistoryResponse>,
    val nextCursor: String?,
) {
    companion object {
        fun from(dto: FinancialProductHistorySliceDto): FinancialProductHistorySliceResponse =
            FinancialProductHistorySliceResponse(
                items = dto.items.map(FinancialProductHistoryResponse::from),
                nextCursor = dto.nextCursor,
            )
    }
}

data class FinancialProductRateHistoryResponse(
    val observedAt: OffsetDateTime,
    val financialProductId: Long,
    val financialProductOptionId: Long?,
    val interestRateType: String,
    val reserveType: String?,
    val depositPeriodMonths: Int,
    val baseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
    val payload: String?,
) {
    companion object {
        fun from(dto: FinancialProductRateHistoryDto): FinancialProductRateHistoryResponse =
            FinancialProductRateHistoryResponse(
                observedAt = dto.observedAt,
                financialProductId = dto.financialProductId,
                financialProductOptionId = dto.financialProductOptionId,
                interestRateType = dto.interestRateType,
                reserveType = dto.reserveType,
                depositPeriodMonths = dto.depositPeriodMonths,
                baseInterestRate = dto.baseInterestRate,
                maximumInterestRate = dto.maximumInterestRate,
                payload = dto.payload,
            )
    }
}
