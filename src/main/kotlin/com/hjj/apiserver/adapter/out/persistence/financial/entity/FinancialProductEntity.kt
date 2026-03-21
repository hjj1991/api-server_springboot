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
        Index(name = "ix_fin_product__company_id", columnList = "financial_company_id"),
        Index(name = "ix_fin_product__product_code", columnList = "financial_product_code"),
        Index(name = "ix_fin_product__product_name", columnList = "financial_product_name"),
        Index(
            name = "ix_fin_product__product_type_status_last_seen_at",
            columnList = "financial_product_type,status,last_seen_at",
        ),
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
    financialSubmitDay: OffsetDateTime? = null,
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

    @Column(name = "financial_product_code", length = 100)
    var financialProductCode: String = financialProductCode
        protected set

    @Column(name = "financial_product_name")
    var financialProductName: String = financialProductName
        protected set

    @Column(name = "join_way", columnDefinition = "text")
    var joinWay: String? = joinWay
        protected set

    @Column(name = "post_maturity_interest_rate", columnDefinition = "text")
    var postMaturityInterestRate: String? = postMaturityInterestRate
        protected set

    @Column(name = "special_condition", columnDefinition = "text")
    var specialCondition: String? = specialCondition
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "join_restriction", length = 50)
    var joinRestriction: JoinRestriction = joinRestriction
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_product_type", length = 50)
    var financialProductType: FinancialProductType = financialProductType
        protected set

    @Column(name = "join_member", columnDefinition = "text")
    var joinMember: String = joinMember
        protected set

    @Column(name = "additional_notes", columnDefinition = "text")
    var additionalNotes: String = additionalNotes
        protected set

    @Column(name = "max_limit")
    var maxLimit: Long? = maxLimit
        protected set

    @Column(name = "dcls_month", length = 6)
    var dclsMonth: String = dclsMonth
        protected set

    @Column(name = "dcls_start_day", columnDefinition = "date")
    var dclsStartDay: LocalDate = dclsStartDay
        protected set

    @Column(name = "dcls_end_day", columnDefinition = "date")
    var dclsEndDay: LocalDate? = dclsEndDay
        protected set

    @Column(name = "financial_submit_day", columnDefinition = "timestamptz")
    var financialSubmitDay: OffsetDateTime? = financialSubmitDay
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    var status: ProductStatus = status
        protected set

    @Column(name = "last_seen_at", columnDefinition = "timestamptz")
    var lastSeenAt: OffsetDateTime? = lastSeenAt
        protected set

    @Column(name = "product_content_hash", length = 64)
    var productContentHash: String? = productContentHash
        protected set

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_vector", columnDefinition = "vector(768)")
    var embeddingVector: FloatArray? = embeddingVector
        protected set

    @Column(name = "source_payload", columnDefinition = "text")
    var sourcePayload: String? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_company_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
