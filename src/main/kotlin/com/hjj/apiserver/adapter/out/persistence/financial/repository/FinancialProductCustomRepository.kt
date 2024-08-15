package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.hibernate.Hibernate
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class FinancialProductCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    @Transactional(readOnly = true)
    fun fetchFinancialProductsWithPaginationInfo(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Pair<List<FinancialProductEntity>, Boolean> {
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

        pageable.sort.forEach { order ->
            val pathBuilder = PathBuilder(qFinancialProduct.type, qFinancialProduct.metadata)
            val path =
                when (order.property) {
                    "companyName" -> qFinancialCompany.companyName // Example for related field
                    "depositPeriodMonths" -> qFinancialProductOption.depositPeriodMonths
                    "baseInterestRate" -> qFinancialProductOption.baseInterestRate
                    "maximumInterestRate" -> qFinancialProductOption.maximumInterestRate
                    else -> pathBuilder.get(order.property) as Path<Comparable<*>>
                }

            val sort =
                if (order.isAscending) {
                    Order.ASC
                } else {
                    Order.DESC
                }
            jpaQuery.orderBy(OrderSpecifier(sort, path))
        }

        val content = jpaQuery.fetch()

        content.forEach { financialProduct ->
            Hibernate.initialize(financialProduct.financialProductOptionEntities)
        }

        val hasNext =
            jpaQuery.clone()
                .offset(pageable.offset + pageable.pageSize.toLong())
                .limit(1)
                .fetchFirst() != null

        return Pair(content, hasNext)
    }
}
