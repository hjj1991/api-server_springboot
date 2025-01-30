package com.hjj.apiserver.adapter.out.persistence.financial.converter

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialCompanyEntity
import com.hjj.apiserver.domain.financial.FinancialCompany
import org.springframework.stereotype.Component

@Component
class FinancialCompanyMapper {
    fun mapToDomainEntity(financialCompanyEntity: FinancialCompanyEntity): FinancialCompany =
        FinancialCompany(
            financialCompanyId = financialCompanyEntity.financialCompanyId,
            financialCompanyCode = financialCompanyEntity.financialCompanyCode,
            dclsMonth = financialCompanyEntity.dclsMonth,
            companyName = financialCompanyEntity.companyName,
            dclsChrgMan = financialCompanyEntity.dclsChrgMan,
            hompUrl = financialCompanyEntity.hompUrl,
            calTel = financialCompanyEntity.calTel,
            financialGroupType = financialCompanyEntity.financialGroupType,
        )
}
