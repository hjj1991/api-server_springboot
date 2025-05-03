package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialCompanyMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductMapper
import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductCustomRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductRepository
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import com.hjj.apiserver.domain.financial.FinancialProduct
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
class FinancialProductPersistenceAdapter(
    val financialProductRepository: FinancialProductRepository,
    val financialProductCustomRepository: FinancialProductCustomRepository,
    val financialCompanyMapper: FinancialCompanyMapper,
    val financialProductMapper: FinancialProductMapper,
) : GetFinancialProductPort {

    override fun findFinancialProductsByCondition(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): List<FinancialProduct> {
        val financialProductEntities =
            this.financialProductCustomRepository.findByCondition(
                condition = financialProductSearchCondition,
                pageable = pageable,
            )

        return financialProductEntities.map { financialProductEntity ->
            this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity).apply {
                financialCompany = financialCompanyMapper.mapToDomainEntity(financialProductEntity.financialCompanyEntity)
            }
        }

    }

    override fun existsNextPageByCondition(financialProductSearchCondition: FinancialProductSearchCondition, pageable: Pageable): Boolean =
        this.financialProductCustomRepository.existsNextPageByCondition(financialProductSearchCondition, pageable)

    @Transactional(readOnly = true)
    override fun findFinancialProduct(financialProductId: Long): FinancialProduct {
        val financialProductEntity = (
            this.financialProductRepository.findByIdOrNull(financialProductId)
                ?: throw FinancialProductNotFoundException(message = "FinancialProduct not found financialProductId: $financialProductId")
            )

        return this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity).apply {
            financialCompany = financialCompanyMapper.mapToDomainEntity(financialProductEntity.financialCompanyEntity)
        }
    }
}
