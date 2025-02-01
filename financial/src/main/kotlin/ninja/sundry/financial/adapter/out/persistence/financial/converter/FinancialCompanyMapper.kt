package ninja.sundry.financial.adapter.out.persistence.financial.converter

import domain.financial.FinancialCompany
import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialCompanyEntity
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
