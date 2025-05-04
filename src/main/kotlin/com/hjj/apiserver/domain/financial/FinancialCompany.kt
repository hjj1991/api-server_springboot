package com.hjj.apiserver.domain.financial

data class FinancialCompany(
    val financialCompanyId: Long = 0L,
    val financialCompanyCode: String,
    val dclsMonth: String,
    val companyName: String,
    val dclsChrgMan: String? = null,
    val hompUrl: String? = null,
    val calTel: String? = null,
    val financialGroupType: FinancialGroupType,
)
