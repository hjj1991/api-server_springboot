package com.hjj.apiserver.application.port.input.financial

import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface GetFinancialUseCase {
    fun getFinancialsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        query: String?,
        status: ProductStatus,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Slice<FinancialProduct>

    fun getFinancialProduct(financialProductId: Long): FinancialProduct
}
