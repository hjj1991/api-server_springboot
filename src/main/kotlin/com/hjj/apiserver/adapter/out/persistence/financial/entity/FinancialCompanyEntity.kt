package com.hjj.apiserver.adapter.out.persistence.financial.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.financial.FinancialGroupType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "financial_company",
    indexes = [
        Index(columnList = "financialCompanyCode"),
        Index(columnList = "companyName"),

    ],
)
class FinancialCompanyEntity(
    financialCompanyId: Long = 0L,
    financialCompanyCode: String,
    dclsMonth: String,
    companyName: String,
    dclsChrgMan: String? = null,
    hompUrl: String? = null,
    calTel: String? = null,
    financialGroupType: FinancialGroupType,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var financialCompanyId: Long = financialCompanyId
        protected set

    @Column(length = 20, unique = true)
    var financialCompanyCode: String = financialCompanyCode
        protected set

    @Column(length = 6)
    var dclsMonth: String = dclsMonth
        protected set

    var companyName: String = companyName
        protected set

    var dclsChrgMan: String? = dclsChrgMan
        protected set

    @Column(length = 1024)
    var hompUrl: String? = hompUrl
        protected set

    @Column(length = 100)
    var calTel: String? = calTel
        protected set

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    var financialGroupType: FinancialGroupType = financialGroupType
        protected set

    @Column(columnDefinition = "text")
    var sourcePayload: String? = null
        protected set
}
