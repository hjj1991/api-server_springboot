package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.financial.request.FinancialProductListRequest
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import jakarta.validation.Valid
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
        @Valid request: FinancialProductListRequest,
        @PageableDefault(page = 0, size = 20, sort = [DEFAULT_SORT_FIELD], direction = Sort.Direction.DESC) pageable: Pageable,
    ): Slice<FinancialProductResponse> =
        this.getFinancialUseCase
            .getFinancialsWithPaginationInfo(
                financialGroupType = request.financialGroupType,
                companyName = request.companyName,
                joinRestriction = request.joinRestriction,
                financialProductType = request.financialProductType,
                financialProductName = request.financialProductName,
                query = request.q,
                status = request.status,
                depositPeriodMonths = request.depositPeriodMonths,
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
        const val DEFAULT_SORT_FIELD = "lastSeenAt"
    }
}
