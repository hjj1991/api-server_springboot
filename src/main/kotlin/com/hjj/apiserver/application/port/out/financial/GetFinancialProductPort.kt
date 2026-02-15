package com.hjj.apiserver.application.port.out.financial

import com.hjj.apiserver.domain.financial.FinancialProduct

interface GetFinancialProductPort {
    fun findFinancialProduct(financialProductId: Long): FinancialProduct

    fun findFinancialProductsByIds(ids: List<Long>): List<FinancialProduct>
}
