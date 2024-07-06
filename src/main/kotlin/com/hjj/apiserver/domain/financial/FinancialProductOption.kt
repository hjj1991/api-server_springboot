package com.hjj.apiserver.domain.financial

import java.math.BigDecimal

class FinancialProductOption(
    financialProductOptionId: Long = 0L,
    interestRateType: InterestRateType,
    reserveType: ReserveType?,
    depositPeriodMonths: String,
    baseInterestRate: BigDecimal?,
    maximumInterestRate: BigDecimal?,
    financialProduct: FinancialProduct?,
) {
    var financialProductOptionId: Long = financialProductOptionId
        private set

    var interestRateType: InterestRateType = interestRateType
        private set

    var reserveType: ReserveType? = reserveType
        private set

    var depositPeriodMonths: String = depositPeriodMonths
        private set

    var baseInterestRate: BigDecimal? = baseInterestRate
        private set

    var maximumInterestRate: BigDecimal? = maximumInterestRate
        private set

    var financialProduct: FinancialProduct? = financialProduct
}
