package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.adapter.out.persistence.financial.document.FinancialProductDocument
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

        fun from(option: FinancialProductDocument.Option): FinancialProductOptionResponse {
            return FinancialProductOptionResponse(
                interestRateType = option.interestRateType,
                reserveType = option.reserveType,
                depositPeriodMonths = option.depositPeriodMonths,
                baseInterestRate = option.initRate.toBigDecimal(),
                maximumInterestRate = option.maxRate.toBigDecimal(),
            )
        }
    }
}
