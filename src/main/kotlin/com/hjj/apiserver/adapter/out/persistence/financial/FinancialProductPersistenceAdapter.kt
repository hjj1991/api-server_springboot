package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductMapper
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductRepository
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import com.hjj.apiserver.config.CacheConfig
import com.hjj.apiserver.config.CacheConfig.Companion.FINANCIAL_PRODUCT
import com.hjj.apiserver.config.CacheKeyConfig
import com.hjj.apiserver.domain.financial.FinancialProduct
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
class FinancialProductPersistenceAdapter(
    val financialProductRepository: FinancialProductRepository,
    val financialProductMapper: FinancialProductMapper,
) : GetFinancialProductPort {
    @Transactional(readOnly = true)
    @Cacheable(
        cacheManager = CacheConfig.REDIS_CACHE_MANAGER,
        cacheNames = [FINANCIAL_PRODUCT],
        keyGenerator = CacheKeyConfig.PARAMS_LOCAL_DATE,
    )
    override fun findFinancialProduct(financialProductId: Long): FinancialProduct {
        val financialProductEntity = (
            this.financialProductRepository.findByIdOrNull(financialProductId)
                ?: throw FinancialProductNotFoundException(message = "FinancialProduct not found financialProductId: $financialProductId")
        )

        return this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity)
    }

    override fun findFinancialProductsByIds(ids: List<Long>): List<FinancialProduct> {
        val financialProductEntities = this.financialProductRepository.findAllById(ids)
        return financialProductEntities.map { financialProductEntity ->
            this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity)
        }
    }
}
