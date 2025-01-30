package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialCompanyMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductMapper
import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductCustomRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductRepository
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
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
    @Transactional(readOnly = true)
    override fun findFinancialProductsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Pair<List<FinancialProduct>, Boolean> {
        val financialProductSearchCondition =
            FinancialProductSearchCondition(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
            )

        val financialProductEntitiesWithPaginationInfo =
            this.financialProductCustomRepository.fetchFinancialProductsWithPaginationInfo(
                financialProductSearchCondition = financialProductSearchCondition,
                pageable = pageable,
            )

        return Pair(
            financialProductEntitiesWithPaginationInfo.first.map { financialProductEntity ->
                this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity).apply {
                    financialCompany = financialCompanyMapper.mapToDomainEntity(financialProductEntity.financialCompanyEntity)
                }
            },
            financialProductEntitiesWithPaginationInfo.second,
        )
    }

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
