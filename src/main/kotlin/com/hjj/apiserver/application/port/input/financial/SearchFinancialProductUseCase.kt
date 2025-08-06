package com.hjj.apiserver.application.port.input.financial

import com.hjj.apiserver.dto.financial.FinancialProductSearchResponse

fun interface SearchFinancialProductUseCase {

    fun searchFinancialProduct(query: String): FinancialProductSearchResponse
}
