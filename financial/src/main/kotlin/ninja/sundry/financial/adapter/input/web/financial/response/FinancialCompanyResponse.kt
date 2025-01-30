package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialCompany

data class FinancialCompanyResponse(
    val dclsMonth: String,
    val companyName: String,
    val dclsChrgMan: String?,
    val hompUrl: String?,
    val calTel: String?,
    val financialGroupType: String,
) {
    companion object {
        fun from(financialCompany: FinancialCompany) =
            FinancialCompanyResponse(
                dclsMonth = financialCompany.dclsMonth,
                companyName = financialCompany.companyName,
                dclsChrgMan = financialCompany.dclsChrgMan,
                hompUrl = financialCompany.hompUrl,
                calTel = financialCompany.calTel,
                financialGroupType = financialCompany.financialGroupType.title,
            )
    }
}
