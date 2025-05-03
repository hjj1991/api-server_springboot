package com.hjj.apiserver.application.service.financial

import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductResponse
import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.config.CacheConfig
import com.hjj.apiserver.config.CacheConfig.Companion.FINANCIAL_PRODUCT
import com.hjj.apiserver.config.CacheConfig.Companion.FINANCIAL_PRODUCTS
import com.hjj.apiserver.config.CacheKeyConfig
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinancialService(
    private val getFinancialProductPort: GetFinancialProductPort,
) : GetFinancialUseCase {
    @Cacheable(
        cacheManager = CacheConfig.REDIS_CACHE_MANAGER,
        cacheNames = [FINANCIAL_PRODUCTS],
        keyGenerator = CacheKeyConfig.PARAMS_LOCAL_DATE,
    )
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
        val searchCondition =
            FinancialProductSearchCondition(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
            )
        val financialProducts =
            this.getFinancialProductPort.findFinancialProductsByCondition(
                financialProductSearchCondition = searchCondition,
                pageable = pageable,
            )
        val hasNext =
            this.getFinancialProductPort.existsNextPageByCondition(
                financialProductSearchCondition = searchCondition,
                pageable = pageable,
            )
        return SliceImpl(
            financialProducts.map(FinancialProductResponse::from),
            pageable,
            hasNext,
        )
    }

    @Cacheable(
        cacheManager = CacheConfig.REDIS_CACHE_MANAGER,
        cacheNames = [FINANCIAL_PRODUCT],
        keyGenerator = CacheKeyConfig.PARAMS_LOCAL_DATE,
    )
    override fun getFinancialProduct(financialProductId: Long): FinancialProduct =
        this.getFinancialProductPort.findFinancialProduct(financialProductId = financialProductId)
}
