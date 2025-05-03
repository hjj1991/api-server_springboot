package ninja.sundry.financial.adapter.out.persistence.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProduct
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import ninja.sundry.financial.adapter.out.persistence.financial.converter.FinancialCompanyMapper
import ninja.sundry.financial.adapter.out.persistence.financial.converter.FinancialProductMapper
import ninja.sundry.financial.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import ninja.sundry.financial.adapter.out.persistence.financial.repository.FinancialProductCustomRepository
import ninja.sundry.financial.adapter.out.persistence.financial.repository.FinancialProductRepository
import ninja.sundry.financial.application.port.out.financial.GetFinancialProductPort
import ninja.sundry.financial.common.PersistenceAdapter
import ninja.sundry.financial.common.exception.financial.FinancialProductNotFoundException
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

    override fun findFinancialProductsWithPagination(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): List<FinancialProduct> {
        val financialProductSearchCondition =
            FinancialProductSearchCondition(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
            )

        val financialProductEntitiesWithPagination =
            this.financialProductCustomRepository.findFinancialProductsWithPagination(
                financialProductSearchCondition = financialProductSearchCondition,
                pageable = pageable,
            )

        return financialProductEntitiesWithPagination.map { financialProductEntity ->
            this.financialProductMapper.mapToDomainEntity(financialProductEntity = financialProductEntity).apply {
                financialCompany = financialCompanyMapper.mapToDomainEntity(financialProductEntity.financialCompanyEntity)
            }
        }

    }

    override fun hasNextFinancialProductsWithPagination(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Boolean {
        val financialProductSearchCondition =
            FinancialProductSearchCondition(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
            )
        return this.financialProductCustomRepository.hasNextFinancialProductsWithPagination(
            financialProductSearchCondition = financialProductSearchCondition,
            pageable = pageable,
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
