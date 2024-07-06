package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialCompany
import com.hjj.apiserver.domain.financial.FinancialGroupType

data class FinancialCompanyResponse(
    val financialCompanyId: Long,
    val financialCompanyCode: String,
    val dclsMonth: String,
    val companyName: String,
    val dclsChrgMan: String?,
    val hompUrl: String?,
    val calTel: String?,
    val financialGroupType: FinancialGroupType,
) {
    companion object {
        fun from(financialCompany: FinancialCompany) =
            FinancialCompanyResponse(
                financialCompanyId = financialCompany.financialCompanyId,
                financialCompanyCode = financialCompany.financialCompanyCode,
                dclsMonth = financialCompany.dclsMonth,
                companyName = financialCompany.companyName,
                dclsChrgMan = financialCompany.dclsChrgMan,
                hompUrl = financialCompany.hompUrl,
                calTel = financialCompany.calTel,
                financialGroupType = financialCompany.financialGroupType,
            )
    }
}
