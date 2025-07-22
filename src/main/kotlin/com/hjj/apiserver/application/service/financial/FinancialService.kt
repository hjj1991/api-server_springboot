package com.hjj.apiserver.application.service.financial

import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.application.port.out.financial.SearchFinancialProductPort
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinancialService(
    private val getFinancialProductPort: GetFinancialProductPort,
    private val searchFinancialProductPort: SearchFinancialProductPort,
) : GetFinancialUseCase {
    @Transactional(readOnly = true)
    override fun getFinancialsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Slice<FinancialProductResponse> {
        val financialProductIds = searchFinancialProductPort.searchFinancialProducts(
            financialGroupType = financialGroupType,
            companyName = companyName,
            joinRestriction = joinRestriction,
            financialProductType = financialProductType,
            financialProductName = financialProductName,
            depositPeriodMonths = depositPeriodMonths,
            pageable = pageable,
        )

        val financialProducts = getFinancialProductPort.findFinancialProductsByIds(financialProductIds.content)
        val financialProductsMap = financialProducts.associateBy { it.financialProductId }
        val sortedFinancialProducts = financialProductIds.content.mapNotNull { financialProductsMap[it] }


        return SliceImpl(
            sortedFinancialProducts.map { FinancialProductResponse.from(it) },
            pageable,
            financialProductIds.hasNext(),
        )
    }

    override fun getFinancialProduct(financialProductId: Long): FinancialProduct =
        this.getFinancialProductPort.findFinancialProduct(financialProductId = financialProductId)
}
