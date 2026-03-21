package com.hjj.apiserver.adapter.out.persistence.financial.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDate
import java.time.OffsetDateTime

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
    dclsMonth: String,
    dclsStartDay: LocalDate,
    dclsEndDay: LocalDate? = null,
    financialSubmitDay: OffsetDateTime,
    financialCompanyEntity: FinancialCompanyEntity,
    status: ProductStatus,
    lastSeenAt: OffsetDateTime? = null,
    productContentHash: String? = null,
    embeddingVector: FloatArray? = null,
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

    @Column(columnDefinition = "text")
    var joinWay: String? = joinWay
        protected set

    @Column(columnDefinition = "text")
    var postMaturityInterestRate: String? = postMaturityInterestRate
        protected set

    @Column(columnDefinition = "text")
    var specialCondition: String? = specialCondition
        protected set

    @Enumerated(EnumType.STRING)
    var joinRestriction: JoinRestriction = joinRestriction
        protected set

    @Enumerated(EnumType.STRING)
    var financialProductType: FinancialProductType = financialProductType
        protected set

    @Column(columnDefinition = "text")
    var joinMember: String = joinMember
        protected set

    @Column(columnDefinition = "text")
    var additionalNotes: String = additionalNotes
        protected set

    var maxLimit: Long? = maxLimit
        protected set

    var dclsMonth: String = dclsMonth
        protected set

    @Column(columnDefinition = "date")
    var dclsStartDay: LocalDate = dclsStartDay
        protected set

    @Column(columnDefinition = "date")
    var dclsEndDay: LocalDate? = dclsEndDay
        protected set

    @Column(columnDefinition = "timestamptz")
    var financialSubmitDay: OffsetDateTime = financialSubmitDay
        protected set

    @Enumerated(EnumType.STRING)
    var status: ProductStatus = status
        protected set

    var lastSeenAt: OffsetDateTime? = lastSeenAt
        protected set

    @Column(length = 255) // SHA-256 hash is 64 characters long
    var productContentHash: String? = productContentHash
        protected set

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    var embeddingVector: FloatArray? = embeddingVector
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financialCompanyId", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var financialCompanyEntity: FinancialCompanyEntity = financialCompanyEntity
        protected set

    @OneToMany(mappedBy = "financialProductEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var financialProductOptionEntities: MutableList<FinancialProductOptionEntity> = financialProductOptionEntities

    fun updateEmbeddingVector(embeddingVector: FloatArray) {
        this.embeddingVector = embeddingVector
    }

    fun updateProductContentHash(productContentHash: String) {
        this.productContentHash = productContentHash
    }
}
