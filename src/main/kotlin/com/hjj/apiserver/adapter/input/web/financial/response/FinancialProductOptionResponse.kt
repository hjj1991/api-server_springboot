package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialProductOption
import java.math.BigDecimal

data class FinancialProductOptionResponse(
    val interestRateType: String,
    val reserveType: String?,
    val depositPeriodMonths: String,
    val baseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
) {
    companion object {
        fun from(financialProductOption: FinancialProductOption): FinancialProductOptionResponse {
            return FinancialProductOptionResponse(
                interestRateType = financialProductOption.interestRateType.description,
                reserveType = financialProductOption.reserveType?.description,
                depositPeriodMonths = financialProductOption.depositPeriodMonths,
                baseInterestRate = financialProductOption.baseInterestRate,
                maximumInterestRate = financialProductOption.maximumInterestRate,
            )
        }
    }
}
