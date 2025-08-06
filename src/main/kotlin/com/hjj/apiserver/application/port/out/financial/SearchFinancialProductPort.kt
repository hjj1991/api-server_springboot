package com.hjj.apiserver.application.port.out.financial

import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.dto.financial.ProductSearchResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SearchFinancialProductPort {

    fun searchFinancialProducts(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Slice<Long>

    fun searchFinancialProduct(query: String): ProductSearchResponse
}
