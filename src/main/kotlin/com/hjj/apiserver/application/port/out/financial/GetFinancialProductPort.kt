package com.hjj.apiserver.application.port.out.financial

import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.domain.financial.FinancialProduct
import org.springframework.data.domain.Pageable

interface GetFinancialProductPort {
    fun findFinancialProductsByCondition(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): List<FinancialProduct>

    fun existsNextPageByCondition(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Boolean

    fun findFinancialProduct(financialProductId: Long): FinancialProduct
}
