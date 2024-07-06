package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialProductOption
import com.hjj.apiserver.domain.financial.InterestRateType
import com.hjj.apiserver.domain.financial.ReserveType
import java.math.BigDecimal

data class FinancialProductOptionResponse(
    val financialProductOptionId: Long,
    val interestRateType: InterestRateType,
    val reserveType: ReserveType?,
    val depositPeriodMonths: String,
    val baseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
) {
    companion object {
        fun from(financialProductOption: FinancialProductOption): FinancialProductOptionResponse {
            return FinancialProductOptionResponse(
                financialProductOptionId = financialProductOption.financialProductOptionId,
                interestRateType = financialProductOption.interestRateType,
                reserveType = financialProductOption.reserveType,
                depositPeriodMonths = financialProductOption.depositPeriodMonths,
                baseInterestRate = financialProductOption.baseInterestRate,
                maximumInterestRate = financialProductOption.maximumInterestRate,
            )
        }
    }
}
