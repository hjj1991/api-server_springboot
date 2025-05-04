package com.hjj.apiserver.domain.financial

data class FinancialProduct(
    val financialProductId: Long = 0L,
    val financialProductCode: String,
    val financialProductName: String,
    val joinWay: String? = null,
    val postMaturityInterestRate: String? = null,
    val specialCondition: String? = null,
    val joinRestriction: JoinRestriction,
    val financialProductType: FinancialProductType,
    val joinMember: String,
    val additionalNotes: String,
    val maxLimit: Long? = null,
    val dclsMonth: String? = null,
    val dclsStartDay: String? = null,
    val dclsEndDay: String? = null,
    val financialSubmitDay: String? = null,
    val financialCompany: FinancialCompany? = null,
    val financialProductOptions: MutableList<FinancialProductOption> = mutableListOf(),
)
