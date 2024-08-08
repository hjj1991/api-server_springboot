package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialCompanyMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductOptionMapper
import com.hjj.apiserver.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class FinancialProductCustomRepository(
    private val jpaQueryFactory: JPAQueryFactory,
    private val financialCompanyMapper: FinancialCompanyMapper,
    private val financialProductMapper: FinancialProductMapper,
    private val financialProductOptionMapper: FinancialProductOptionMapper,
) {
    @Transactional(readOnly = true)
    fun fetchFinancialProductsWithPaginationInfo(
        financialProductSearchCondition: FinancialProductSearchCondition,
        pageable: Pageable,
    ): Pair<List<FinancialProduct>, Boolean> {
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

        val financialProducts = jpaQuery.fetch()

        // Get the IDs of the fetched FinancialProducts
        val financialProductIds = financialProducts.map { it.financialProductId }

        // BooleanBuilder를 사용하여 조건을 동적으로 추가
        val booleanBuilder = BooleanBuilder()

        // 공통 조건 추가
        booleanBuilder.and(qFinancialProductOption.financialProductEntity.financialProductId.`in`(financialProductIds))

        // depositPeriodMonths가 null이 아닌 경우에만 조건 추가
        if (financialProductSearchCondition.depositPeriodMonths != null) {
            booleanBuilder.and(qFinancialProductOption.depositPeriodMonths.eq(financialProductSearchCondition.depositPeriodMonths))
        }

        // Second query: Fetch FinancialProductOptions for the previously fetched FinancialProducts
        val optionsQuery =
            this.jpaQueryFactory
                .selectFrom(qFinancialProductOption)
                .where(booleanBuilder)

        val options = optionsQuery.fetch()

        // Map FinancialProductOptions to their respective FinancialProducts
        val productOptionsMap = options.groupBy { it.financialProductEntity.financialProductId }
        val content =
            financialProducts.map { financialProductEntity ->
                val financialProductOptionEntities =
                    productOptionsMap[financialProductEntity.financialProductId]?.toMutableList() ?: mutableListOf()
                this.financialProductMapper.mapToDomainEntity(
                    financialProductEntity = financialProductEntity,
                    financialProductOptions =
                        financialProductOptionEntities.map {
                            this.financialProductOptionMapper.mapToDomainEntity(
                                it,
                            )
                        },
                )
                    .apply {
                        financialCompany = financialCompanyMapper.mapToDomainEntity(financialProductEntity.financialCompanyEntity)
                    }
            }
        val hasNext =
            jpaQuery.clone()
                .offset(pageable.offset + pageable.pageSize.toLong())
                .limit(1)
                .fetchFirst() != null

        return Pair(content, hasNext)
    }
}
