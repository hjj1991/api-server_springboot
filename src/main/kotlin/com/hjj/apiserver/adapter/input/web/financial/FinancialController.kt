package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FinancialController(
    private val getFinancialUseCase: GetFinancialUseCase,
) {
    @GetMapping("v1/financials")
    fun getFinancials(
        @RequestParam(required = false) financialGroupType: FinancialGroupType?,
        @RequestParam(required = false) companyName: String?,
        @RequestParam(required = false) joinRestriction: JoinRestriction?,
        @RequestParam(required = false) financialProductType: FinancialProductType?,
        @RequestParam(required = false) financialProductName: String?,
        @RequestParam(required = false) depositPeriodMonths: String?,
        @PageableDefault(page = 0, size = 20) pageable: Pageable,
    ): Slice<FinancialProductResponse> {
        val financialProductsWithPaginationInfo =
            this.getFinancialUseCase.getFinancialsWithPaginationInfo(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
                pageable = pageable,
            )

        return SliceImpl(
            financialProductsWithPaginationInfo.first.map {
                FinancialProductResponse.from(it)
            },
            pageable,
            financialProductsWithPaginationInfo.second,
        )
    }

    @GetMapping("v1/financialProducts/{financialProductId}")
    fun getFinancialProduct(
        @PathVariable financialProductId: Long,
    ): FinancialProductResponse {
        val financialProduct = this.getFinancialUseCase.getFinancialProduct(financialProductId = financialProductId)
        return FinancialProductResponse.from(financialProduct)
    }
}
