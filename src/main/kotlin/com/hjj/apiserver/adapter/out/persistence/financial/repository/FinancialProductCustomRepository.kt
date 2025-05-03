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
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.hibernate.Hibernate
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class FinancialProductCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    private val product = QFinancialProductEntity.financialProductEntity
    private val company = QFinancialCompanyEntity.financialCompanyEntity
    private val option = QFinancialProductOptionEntity.financialProductOptionEntity

    /**
     * 조건에 맞는 금융 상품 목록을 조회합니다.
     */
    fun findByCondition(
        condition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): List<FinancialProductEntity> {
        val base =
            buildBaseQuery(condition)
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .distinct()

        applySorting(base, pageable)

        val results = base.fetch()
        results.forEach { Hibernate.initialize(it.financialProductOptionEntities) }
        return results
    }

    /**
     * 다음 페이지의 존재 여부를 확인합니다.
     */
    fun existsNextPageByCondition(
        condition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Boolean {
        val hasNext =
            buildBaseQuery(condition)
                .offset(pageable.offset + pageable.pageSize)
                .limit(1)
                .distinct()
                .fetchFirst() != null
        return hasNext
    }

    /**
     * 공통 쿼리 빌더
     */
    private fun buildBaseQuery(condition: FinancialProductSearchCondition): JPAQuery<FinancialProductEntity> =
        jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.financialCompanyEntity, company).fetchJoin()
            .innerJoin(product.financialProductOptionEntities, option)
            .where(condition.toPredicate())

    /**
     * 정렬 적용
     */
    private fun applySorting(
        query: JPAQuery<*>,
        pageable: Pageable,
    ) {
        pageable.sort.forEach { order ->
            val pathBuilder = PathBuilder(product.type, product.metadata)
            val path =
                when (order.property) {
                    "companyName" -> company.companyName as Path<Comparable<*>>
                    "depositPeriodMonths" -> option.depositPeriodMonths as Path<Comparable<*>>
                    "baseInterestRate" -> option.baseInterestRate as Path<Comparable<*>>
                    "maximumInterestRate" -> option.maximumInterestRate as Path<Comparable<*>>
                    else -> pathBuilder.get(order.property) as Path<Comparable<*>>
                }
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            query.orderBy(OrderSpecifier(direction, path))
        }
    }
}
