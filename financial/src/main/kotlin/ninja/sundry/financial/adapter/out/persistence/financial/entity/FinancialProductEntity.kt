package com.hjj.apiserver.adapter.out.persistence.financial.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "financial_product",
    indexes = [
        Index(columnList = "financialCompanyId"),
        Index(columnList = "financialProductCode"),
        Index(columnList = "financialProductName"),
    ],
)
class FinancialProductEntity(
    financialProductId: Long = 0L,
    financialProductCode: String,
    financialProductName: String,
    joinWay: String? = null,
    postMaturityInterestRate: String? = null,
    specialCondition: String? = null,
    joinRestriction: JoinRestriction,
    financialProductType: FinancialProductType,
    joinMember: String,
    additionalNotes: String,
    maxLimit: Long? = null,
    dclsMonth: String? = null,
    dclsStartDay: String? = null,
    dclsEndDay: String? = null,
    financialSubmitDay: String? = null,
    financialCompanyEntity: FinancialCompanyEntity,
    financialProductOptionEntities: MutableList<FinancialProductOptionEntity> = mutableListOf(),
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var financialProductId: Long = financialProductId
        protected set

    @Column(length = 100)
    var financialProductCode: String = financialProductCode
        protected set

    var financialProductName: String = financialProductName
        protected set

    var joinWay: String? = joinWay
        protected set

    var postMaturityInterestRate: String? = postMaturityInterestRate
        protected set

    var specialCondition: String? = specialCondition
        protected set

    @Enumerated(EnumType.STRING)
    var joinRestriction: JoinRestriction = joinRestriction
        protected set

    @Enumerated(EnumType.STRING)
    var financialProductType: FinancialProductType = financialProductType
        protected set

    var joinMember: String = joinMember
        protected set

    var additionalNotes: String = additionalNotes
        protected set

    var maxLimit: Long? = maxLimit
        protected set

    var dclsMonth: String? = dclsMonth
        protected set

    var dclsStartDay: String? = dclsStartDay
        protected set

    var dclsEndDay: String? = dclsEndDay
        protected set

    var financialSubmitDay: String? = financialSubmitDay
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financialCompanyId", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var financialCompanyEntity: FinancialCompanyEntity = financialCompanyEntity
        protected set

    @OneToMany(mappedBy = "financialProductEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var financialProductOptionEntities: MutableList<FinancialProductOptionEntity> = financialProductOptionEntities
}
