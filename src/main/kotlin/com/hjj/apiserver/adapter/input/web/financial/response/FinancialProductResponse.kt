package com.hjj.apiserver.adapter.input.web.financial.response

import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction

data class FinancialProductResponse(
    val financialProductId: Long,
    val financialProductCode: String,
    val financialProductName: String,
    val joinWay: String?,
    val postMaturityInterestRate: String?,
    val specialCondition: String?,
    val joinRestriction: JoinRestriction,
    val financialProductType: FinancialProductType?,
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
                financialProductCode = financialProduct.financialProductCode,
                financialProductName = financialProduct.financialProductName,
                joinWay = financialProduct.joinWay,
                postMaturityInterestRate = financialProduct.postMaturityInterestRate,
                specialCondition = financialProduct.specialCondition,
                joinRestriction = financialProduct.joinRestriction,
                financialProductType = financialProduct.financialProductType,
                joinMember = financialProduct.joinMember,
                additionalNotes = financialProduct.additionalNotes,
                maxLimit = financialProduct.maxLimit,
                dclsMonth = financialProduct.dclsMonth,
                dclsStartDay = financialProduct.dclsStartDay,
                dclsEndDay = financialProduct.dclsEndDay,
                financialCompany = FinancialCompanyResponse.from(financialProduct.financialCompany!!),
                financialProductOptions = financialProduct.financialProductOptionEntities.map { FinancialProductOptionResponse.from(it) },
            )
    }
}
