package com.hjj.apiserver.adapter.out.persistence.financial.converter

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductEntity
import com.hjj.apiserver.domain.financial.FinancialProduct
import org.springframework.stereotype.Component

@Component
class FinancialProductMapper(
    private val financialProductOptionMapper: FinancialProductOptionMapper,
) {
    fun mapToDomainEntity(financialProductEntity: FinancialProductEntity): FinancialProduct =
        FinancialProduct(
            financialProductId = financialProductEntity.financialProductId,
            financialProductCode = financialProductEntity.financialProductCode,
            financialProductName = financialProductEntity.financialProductName,
            joinWay = financialProductEntity.joinWay,
            postMaturityInterestRate = financialProductEntity.postMaturityInterestRate,
            specialCondition = financialProductEntity.specialCondition,
            joinRestriction = financialProductEntity.joinRestriction,
            financialProductType = financialProductEntity.financialProductType,
            joinMember = financialProductEntity.joinMember,
            additionalNotes = financialProductEntity.additionalNotes,
            maxLimit = financialProductEntity.maxLimit,
            dclsMonth = financialProductEntity.dclsMonth,
            dclsStartDay = financialProductEntity.dclsStartDay,
            dclsEndDay = financialProductEntity.dclsEndDay,
            financialSubmitDay = financialProductEntity.financialSubmitDay,
            financialCompany = null,
            financialProductOptions =
                financialProductEntity.financialProductOptionEntities.map {
                    this.financialProductOptionMapper.mapToDomainEntity(it)
                }.toMutableList(),
        )
}
