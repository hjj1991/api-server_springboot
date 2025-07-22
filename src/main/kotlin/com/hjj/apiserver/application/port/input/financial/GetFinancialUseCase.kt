package com.hjj.apiserver.application.port.input.financial

import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface GetFinancialUseCase {
    fun getFinancialsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Slice<FinancialProductResponse>

    fun getFinancialProduct(financialProductId: Long): FinancialProduct
}
