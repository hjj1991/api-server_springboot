package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class FinancialProductCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    private val product = QFinancialProductEntity.financialProductEntity
    private val company = QFinancialCompanyEntity.financialCompanyEntity
    private val sortingOption = QFinancialProductOptionEntity("sortingOption")

    fun findByCondition(
        condition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): List<Long> =
        buildBaseQuery(condition)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .applySorting(pageable)
            .fetch()

    fun existsNextPageByCondition(
        condition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Boolean =
        buildBaseQuery(condition)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)
            .applySorting(pageable)
            .fetch()
            .size > pageable.pageSize

    private fun buildBaseQuery(condition: FinancialProductSearchCondition): JPAQuery<Long> =
        jpaQueryFactory
            .select(product.financialProductId)
            .from(product)
            .innerJoin(product.financialCompanyEntity, company)
            .where(condition.toPredicate())

    private fun JPAQuery<Long>.applySorting(pageable: Pageable): JPAQuery<Long> {
        pageable.sort.forEach { order ->
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            this.orderBy(resolveOrderSpecifier(order.property, direction))
        }
        return this
    }

    private fun resolveOrderSpecifier(
        property: String,
        direction: Order,
    ): OrderSpecifier<out Comparable<*>> {
        val pathBuilder = PathBuilder(product.type, product.metadata)

        return when (property) {
            "companyName" -> OrderSpecifier(direction, company.companyName)
            "depositPeriodMonths" -> aggregateIntegerOptionOrderSpecifier(direction) { it.depositPeriodMonths.max().coalesce(0) }
            "baseInterestRate" -> aggregateDecimalOptionOrderSpecifier(direction) { it.baseInterestRate.max().coalesce(BigDecimal.ZERO) }
            "maximumInterestRate" -> aggregateDecimalOptionOrderSpecifier(direction) { it.maximumInterestRate.max().coalesce(BigDecimal.ZERO) }
            else -> {
                @Suppress("UNCHECKED_CAST")
                OrderSpecifier(direction, pathBuilder.get(property) as Path<Comparable<*>>)
            }
        }
    }

    private fun aggregateDecimalOptionOrderSpecifier(
        direction: Order,
        selectExpression: (QFinancialProductOptionEntity) -> NumberExpression<BigDecimal>,
    ): OrderSpecifier<BigDecimal> {
        val expression =
            JPAExpressions
                .select(selectExpression(sortingOption))
                .from(sortingOption)
                .where(sortingOption.financialProductEntity.eq(product))

        return OrderSpecifier(direction, expression)
    }

    private fun aggregateIntegerOptionOrderSpecifier(
        direction: Order,
        selectExpression: (QFinancialProductOptionEntity) -> NumberExpression<Int>,
    ): OrderSpecifier<Int> {
        val expression =
            JPAExpressions
                .select(selectExpression(sortingOption))
                .from(sortingOption)
                .where(sortingOption.financialProductEntity.eq(product))

        return OrderSpecifier(direction, expression)
    }
}
