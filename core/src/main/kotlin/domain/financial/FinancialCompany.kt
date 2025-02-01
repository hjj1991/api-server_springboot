package domain.financial

class FinancialCompany(
    financialCompanyId: Long = 0L,
    financialCompanyCode: String,
    dclsMonth: String,
    companyName: String,
    dclsChrgMan: String? = null,
    hompUrl: String? = null,
    calTel: String? = null,
    financialGroupType: FinancialGroupType,
) {
    var financialCompanyId: Long = financialCompanyId
        private set

    var financialCompanyCode: String = financialCompanyCode
        private set

    var dclsMonth: String = dclsMonth
        private set

    var companyName: String = companyName
        private set

    var dclsChrgMan: String? = dclsChrgMan
        private set

    var hompUrl: String? = hompUrl
        private set

    var calTel: String? = calTel
        private set

    var financialGroupType: FinancialGroupType = financialGroupType
        private set
}
