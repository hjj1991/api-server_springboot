package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialProduct

data class FinancialProductResponse(
    val financialProductId: Long,
    val financialProductName: String,
    val joinWay: String?,
    val postMaturityInterestRate: String?,
    val specialCondition: String?,
    val joinRestriction: String,
    val financialProductType: String,
    val joinMember: String,
    val additionalNotes: String,
    val maxLimit: Long?,
    val dclsMonth: String?,
    val dclsStartDay: String?,
    val dclsEndDay: String?,
    val financialCompany: FinancialCompanyResponse,
    val financialProductOptions: List<FinancialProductOptionResponse>,
) {
    companion object {
        fun from(financialProduct: FinancialProduct) =
            FinancialProductResponse(
                financialProductId = financialProduct.financialProductId,
                financialProductName = financialProduct.financialProductName,
                joinWay = financialProduct.joinWay,
                postMaturityInterestRate = financialProduct.postMaturityInterestRate,
                specialCondition = financialProduct.specialCondition,
                joinRestriction = financialProduct.joinRestriction.description,
                financialProductType = financialProduct.financialProductType.description,
                joinMember = financialProduct.joinMember,
                additionalNotes = financialProduct.additionalNotes,
                maxLimit = financialProduct.maxLimit,
                dclsMonth = financialProduct.dclsMonth,
                dclsStartDay = financialProduct.dclsStartDay,
                dclsEndDay = financialProduct.dclsEndDay,
                financialCompany = FinancialCompanyResponse.from(financialProduct.financialCompany!!),
                financialProductOptions = financialProduct.financialProductOptions.map { FinancialProductOptionResponse.from(it) },
            )
    }
}
