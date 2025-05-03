package ninja.sundry.financial.application.service.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProduct
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import ninja.sundry.financial.application.dto.financial.FinancialProductPageResult
import ninja.sundry.financial.application.port.input.financial.GetFinancialUseCase
import ninja.sundry.financial.application.port.out.financial.GetFinancialProductPort
import ninja.sundry.financial.config.CacheConfig
import ninja.sundry.financial.config.CacheConfig.Companion.FINANCIAL_PRODUCT
import ninja.sundry.financial.config.CacheKeyConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinancialService(
    private val getFinancialProductPort: GetFinancialProductPort,
) : GetFinancialUseCase {
//    @Cacheable(
//        cacheManager = CacheConfig.REDIS_CACHE_MANAGER,
//        cacheNames = [FINANCIAL_PRODUCTS],
//        keyGenerator = CacheKeyConfig.PARAMS_LOCAL_DATE,
//    )
    @Transactional(readOnly = true)
    override fun getFinancialsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): FinancialProductPageResult {
        val financialProducts = this.getFinancialProductPort.findFinancialProductsWithPagination(
            financialGroupType = financialGroupType,
            companyName = companyName,
            joinRestriction = joinRestriction,
            financialProductType = financialProductType,
            financialProductName = financialProductName,
            depositPeriodMonths = depositPeriodMonths,
            pageable = pageable,
        )
        val hasMore = this.getFinancialProductPort.hasNextFinancialProductsWithPagination(
            financialGroupType = financialGroupType,
            companyName = companyName,
            joinRestriction = joinRestriction,
            financialProductType = financialProductType,
            financialProductName = financialProductName,
            depositPeriodMonths = depositPeriodMonths,
            pageable = pageable,
        )
        return FinancialProductPageResult(
            financialProducts = financialProducts,
            hasMore = hasMore,
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
