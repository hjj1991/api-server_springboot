package com.hjj.apiserver.domain.financial

import java.math.BigDecimal

data class FinancialProductOption(
    val financialProductOptionId: Long = 0L,
    val interestRateType: InterestRateType,
    val reserveType: ReserveType?,
    val depositPeriodMonths: String,
    val baseInterestRate: BigDecimal?,
    val maximumInterestRate: BigDecimal?,
    val financialProduct: FinancialProduct?,
)
