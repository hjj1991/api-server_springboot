package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductCustomRepository
import com.hjj.apiserver.application.port.out.financial.SearchFinancialProductPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

@PersistenceAdapter
class FinancialProductSearchAdapter(
    private val financialProductCustomRepository: FinancialProductCustomRepository,
) : SearchFinancialProductPort {
    override fun searchFinancialProducts(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        query: String?,
        status: ProductStatus,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Slice<Long> {
        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize, pageable.sort)
        val condition =
            FinancialProductSearchCondition(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                query = query,
                status = status,
                depositPeriodMonths = depositPeriodMonths,
            )

        val financialProductIds = financialProductCustomRepository.findByCondition(condition, pageRequest)
        val hasNext = financialProductCustomRepository.existsNextPageByCondition(condition, pageRequest)

        return SliceImpl(financialProductIds, pageable, hasNext)
    }
}
