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
        Index(name = "ix_fin_company__company_code", columnList = "financial_company_code"),
        Index(name = "ix_fin_company__company_name", columnList = "company_name"),
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

    @Column(name = "financial_company_code", length = 20, unique = true)
    var financialCompanyCode: String = financialCompanyCode
        protected set

    @Column(name = "dcls_month", length = 6)
    var dclsMonth: String = dclsMonth
        protected set

    @Column(name = "company_name")
    var companyName: String = companyName
        protected set

    @Column(name = "dcls_chrg_man")
    var dclsChrgMan: String? = dclsChrgMan
        protected set

    @Column(name = "homp_url", length = 1024)
    var hompUrl: String? = hompUrl
        protected set

    @Column(name = "cal_tel", length = 100)
    var calTel: String? = calTel
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_group_type", length = 50)
    var financialGroupType: FinancialGroupType = financialGroupType
        protected set

    @Column(name = "source_payload", columnDefinition = "text")
    var sourcePayload: String? = null
        protected set
}
