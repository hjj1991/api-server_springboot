package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/financial-products")
class FinancialController(
    private val getFinancialUseCase: GetFinancialUseCase,
) {
    @GetMapping(headers = [ApiVersionConstants.HEADER_V1])
    fun listFinancialProducts(
        @RequestParam(required = false) financialGroupType: FinancialGroupType?,
        @RequestParam(required = false) companyName: String?,
        @RequestParam(required = false) joinRestriction: JoinRestriction?,
        @RequestParam(required = false) financialProductType: FinancialProductType?,
        @RequestParam(required = false) financialProductName: String?,
        @RequestParam(name = "q", required = false) q: String?,
        @RequestParam(defaultValue = DEFAULT_PRODUCT_STATUS) status: ProductStatus,
        @RequestParam(required = false) depositPeriodMonths: String?,
        @PageableDefault(page = 0, size = 20, sort = [DEFAULT_SORT_FIELD], direction = Sort.Direction.DESC) pageable: Pageable,
    ): Slice<FinancialProductResponse> =
        this.getFinancialUseCase
            .getFinancialsWithPaginationInfo(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                query = q,
                status = status,
                depositPeriodMonths = depositPeriodMonths,
                pageable = pageable,
            ).let { financialProducts ->
                SliceImpl(
                    financialProducts.content.map { FinancialProductResponse.from(it) },
                    financialProducts.pageable,
                    financialProducts.hasNext(),
                )
            }

    @GetMapping("/{financialProductId}", headers = [ApiVersionConstants.HEADER_V1])
    fun getFinancialProductById(
        @PathVariable financialProductId: Long,
    ): FinancialProductResponse {
        val financialProduct = this.getFinancialUseCase.getFinancialProduct(financialProductId = financialProductId)
        return FinancialProductResponse.from(financialProduct)
    }

    private companion object {
        const val DEFAULT_PRODUCT_STATUS = "ACTIVE"
        const val DEFAULT_SORT_FIELD = "lastSeenAt"
    }
}
