package com.hjj.apiserver.adapter.out.persistence.financial.dto

import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions

class FinancialProductSearchCondition(
    val financialGroupType: FinancialGroupType?,
    val companyName: String?,
    val joinRestriction: JoinRestriction?,
    val financialProductType: FinancialProductType?,
    val financialProductName: String?,
    val query: String?,
    val status: ProductStatus,
    val depositPeriodMonths: String?,
) {
    fun toPredicate(): Predicate {
        val builder = BooleanBuilder()

        financialGroupType?.let { builder.and(equalFinancialGroupType(it)) }
        companyName?.let { builder.and(likeCompanyName(it)) }
        joinRestriction?.let { builder.and(equalJoinRestriction(it)) }
        financialProductType?.let { builder.and(equalFinancialProductType(it)) }
        financialProductName?.let { builder.and(likeFinancialProductName(it)) }
        query?.takeIf { it.isNotBlank() }?.let { builder.and(matchQuery(it)) }
        builder.and(equalStatus(status))
        depositPeriodMonths?.let { builder.and(equalDepositPeriodMonths(it)) }

        return builder
    }

    private fun equalFinancialGroupType(financialGroupType: FinancialGroupType): BooleanExpression {
        return QFinancialCompanyEntity.financialCompanyEntity.financialGroupType.eq(financialGroupType)
    }

    private fun likeCompanyName(companyName: String): BooleanExpression {
        return QFinancialCompanyEntity.financialCompanyEntity.companyName.like("%$companyName%")
    }

    private fun equalJoinRestriction(joinRestriction: JoinRestriction): BooleanExpression {
        return QFinancialProductEntity.financialProductEntity.joinRestriction.eq(joinRestriction)
    }

    private fun equalFinancialProductType(financialProductType: FinancialProductType): BooleanExpression {
        return QFinancialProductEntity.financialProductEntity.financialProductType.eq(financialProductType)
    }

    private fun likeFinancialProductName(financialProductName: String): BooleanExpression {
        return QFinancialProductEntity.financialProductEntity.financialProductName.containsIgnoreCase(financialProductName)
    }

    private fun equalStatus(status: ProductStatus): BooleanExpression {
        return QFinancialProductEntity.financialProductEntity.status.eq(status)
    }

    private fun matchQuery(query: String): BooleanExpression {
        val normalizedQuery = query.trim()

        val productFullTextMatched =
            Expressions.booleanTemplate(
                """
                to_tsvector(
                    'simple',
                    coalesce({0}, '') || ' ' || coalesce({1}, '') || ' ' || coalesce({2}, '')
                ) @@ websearch_to_tsquery('simple', {3})
                """.trimIndent(),
                QFinancialProductEntity.financialProductEntity.financialProductName,
                QFinancialProductEntity.financialProductEntity.specialCondition,
                QFinancialProductEntity.financialProductEntity.additionalNotes,
                normalizedQuery,
            )

        val companyNameTrigramMatched =
            Expressions.booleanTemplate(
                "{0} % {1}",
                QFinancialCompanyEntity.financialCompanyEntity.companyName,
                normalizedQuery,
            )

        return productFullTextMatched.or(companyNameTrigramMatched)
    }

    private fun equalDepositPeriodMonths(depositPeriodMonths: String): BooleanExpression {
        val parsedDepositPeriodMonths = checkNotNull(depositPeriodMonths.toIntOrNull()) {
            "depositPeriodMonths must be validated before persistence filtering."
        }
        return QFinancialProductOptionEntity.financialProductOptionEntity.depositPeriodMonths.eq(parsedDepositPeriodMonths)
    }
}
