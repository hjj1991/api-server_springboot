package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.web.bind.annotation.GetMapping
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
        @PageableDefault(page = 0, size = 20)
        @SortDefault.SortDefaults(
            SortDefault(sort = ["companyName"], direction = Sort.Direction.ASC),
        ) pageable: Pageable,
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
}
