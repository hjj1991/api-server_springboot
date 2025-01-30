package com.hjj.apiserver.adapter.out.persistence.financial.converter

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
import com.hjj.apiserver.domain.financial.FinancialProductOption
import org.springframework.stereotype.Component

@Component
class FinancialProductOptionMapper {
    fun mapToDomainEntity(financialProductOptionEntity: FinancialProductOptionEntity): FinancialProductOption =
        FinancialProductOption(
            financialProductOptionId = financialProductOptionEntity.financialProductOptionId,
            financialProduct = null,
            interestRateType = financialProductOptionEntity.interestRateType,
            reserveType = financialProductOptionEntity.reserveType,
            depositPeriodMonths = financialProductOptionEntity.depositPeriodMonths,
            baseInterestRate = financialProductOptionEntity.baseInterestRate,
            maximumInterestRate = financialProductOptionEntity.maximumInterestRate,
        )
}
