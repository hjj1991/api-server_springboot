package ninja.sundry.financial.adapter.out.persistence.financial.repository

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import ninja.sundry.financial.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialProductEntity
import ninja.sundry.financial.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import ninja.sundry.financial.adapter.out.persistence.financial.entity.QFinancialProductEntity
import ninja.sundry.financial.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import ninja.sundry.financial.common.exception.financial.UnsupportedSortingFieldException
import org.hibernate.Hibernate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class FinancialProductCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {

    fun hasNextFinancialProductsWithPagination(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Boolean {
        val qFinancialProduct = QFinancialProductEntity.financialProductEntity
        val qFinancialCompany = QFinancialCompanyEntity.financialCompanyEntity
        val qFinancialProductOption = QFinancialProductOptionEntity.financialProductOptionEntity
        return this.jpaQueryFactory
            .selectFrom(qFinancialProduct)
            .innerJoin(qFinancialProduct.financialCompanyEntity, qFinancialCompany)
            .fetchJoin()
            .innerJoin(qFinancialProduct.financialProductOptionEntities, qFinancialProductOption)
            .where(financialProductSearchCondition.toPredicate())
            .distinct()
            .offset(pageable.offset + pageable.pageSize.toLong())
            .limit(1)
            .fetchFirst() != null
    }

    fun findFinancialProductsWithPagination(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): List<FinancialProductEntity> {
        val qFinancialProduct = QFinancialProductEntity.financialProductEntity
        val qFinancialCompany = QFinancialCompanyEntity.financialCompanyEntity
        val qFinancialProductOption = QFinancialProductOptionEntity.financialProductOptionEntity

        val jpaQuery =
            this.jpaQueryFactory
                .selectFrom(qFinancialProduct)
                .innerJoin(qFinancialProduct.financialCompanyEntity, qFinancialCompany)
                .fetchJoin()
                .innerJoin(qFinancialProduct.financialProductOptionEntities, qFinancialProductOption)
                .where(financialProductSearchCondition.toPredicate())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .distinct()
                .orderBy(*createSortingOrdersForFinancialProducts(pageable.sort).toTypedArray())

        val content = jpaQuery.fetch()

        content.forEach { financialProduct ->
            Hibernate.initialize(financialProduct.financialProductOptionEntities)
        }

        return content
    }

    private fun createSortingOrdersForFinancialProducts(
        sort: Sort,
    ): List<OrderSpecifier<*>> {
        val qFinancialCompany = QFinancialCompanyEntity.financialCompanyEntity
        val qFinancialProductOption = QFinancialProductOptionEntity.financialProductOptionEntity

        return sort.mapNotNull { order ->
            val path =
                when (order.property) {
                    "companyName" -> qFinancialCompany.companyName
                    "depositPeriodMonths" -> qFinancialProductOption.depositPeriodMonths
                    "baseInterestRate" -> qFinancialProductOption.baseInterestRate
                    "maximumInterestRate" -> qFinancialProductOption.maximumInterestRate
                    else -> throw UnsupportedSortingFieldException(order.property)
                }
            OrderSpecifier(if (order.isAscending) Order.ASC else Order.DESC, path)
        }
    }
}
