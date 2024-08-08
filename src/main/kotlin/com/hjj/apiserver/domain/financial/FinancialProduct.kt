package com.hjj.apiserver.domain.financial

class FinancialProduct(
    financialProductId: Long = 0L,
    financialProductCode: String,
    financialProductName: String,
    joinWay: String? = null,
    postMaturityInterestRate: String? = null,
    specialCondition: String? = null,
    joinRestriction: JoinRestriction,
    financialProductType: FinancialProductType,
    joinMember: String,
    additionalNotes: String,
    maxLimit: Long? = null,
    dclsMonth: String? = null,
    dclsStartDay: String? = null,
    dclsEndDay: String? = null,
    financialSubmitDay: String? = null,
    financialCompany: FinancialCompany? = null,
    financialProductOptions: MutableList<FinancialProductOption> = mutableListOf(),
) {
    var financialProductId: Long = financialProductId
        private set

    var financialProductCode: String = financialProductCode
        private set

    var financialProductName: String = financialProductName
        private set

    var joinWay: String? = joinWay
        private set

    var postMaturityInterestRate: String? = postMaturityInterestRate
        private set

    var specialCondition: String? = specialCondition
        private set

    var joinRestriction: JoinRestriction = joinRestriction
        private set

    var financialProductType: FinancialProductType? = financialProductType
        private set

    var joinMember: String = joinMember
        private set

    var additionalNotes: String = additionalNotes
        private set

    var maxLimit: Long? = maxLimit
        private set

    var dclsMonth: String? = dclsMonth
        private set

    var dclsStartDay: String? = dclsStartDay
        private set

    var dclsEndDay: String? = dclsEndDay
        private set

    var financialSubmitDay: String? = financialSubmitDay
        private set

    var financialCompany: FinancialCompany? = financialCompany

    var financialProductOptions: MutableList<FinancialProductOption> = financialProductOptions
        private set
}
