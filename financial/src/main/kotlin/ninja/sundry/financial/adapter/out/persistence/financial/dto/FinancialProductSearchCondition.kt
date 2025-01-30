package com.hjj.apiserver.adapter.out.persistence.financial.dto

import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.QFinancialProductOptionEntity
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression

class FinancialProductSearchCondition(
    val financialGroupType: FinancialGroupType?,
    val companyName: String?,
    val joinRestriction: JoinRestriction?,
    val financialProductType: FinancialProductType?,
    val financialProductName: String?,
    val depositPeriodMonths: String?,
) {
    fun toPredicate(): Predicate {
        val builder = BooleanBuilder()

        financialGroupType?.let { builder.and(equalFinancialGroupType(it)) }
        companyName?.let { builder.and(likeCompanyName(it)) }
        joinRestriction?.let { builder.and(equalJoinRestriction(it)) }
        financialProductType?.let { builder.and(equalFinancialProductType(it)) }
        financialProductName?.let { builder.and(likeFinancialProductName(it)) }
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
        return QFinancialProductEntity.financialProductEntity.financialProductName.like("%$financialProductName%")
    }

    private fun equalDepositPeriodMonths(depositPeriodMonths: String): BooleanExpression {
        return QFinancialProductOptionEntity.financialProductOptionEntity.depositPeriodMonths.eq(depositPeriodMonths)
    }
}
