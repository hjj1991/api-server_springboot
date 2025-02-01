package ninja.sundry.financial.adapter.out.persistence.financial.converter

import domain.financial.FinancialProductOption
import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
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
