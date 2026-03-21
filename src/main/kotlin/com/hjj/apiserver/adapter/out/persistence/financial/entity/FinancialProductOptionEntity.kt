package com.hjj.apiserver.adapter.out.persistence.financial.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.financial.InterestRateType
import com.hjj.apiserver.domain.financial.ReserveType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(
    name = "financial_product_option",
    indexes = [
        Index(name = "ix_fin_product_option__product_id", columnList = "financial_product_id"),
        Index(
            name = "ix_fin_product_option__product_id_period_m_rate_t_reserve_t",
            columnList = "financial_product_id,deposit_period_months,interest_rate_type,reserve_type",
        ),
    ],
)
class FinancialProductOptionEntity(
    financialProductOptionId: Long = 0L,
    interestRateType: InterestRateType,
    reserveType: ReserveType?,
    depositPeriodMonths: Int,
    baseInterestRate: BigDecimal?,
    maximumInterestRate: BigDecimal?,
    financialProductEntity: FinancialProductEntity,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var financialProductOptionId: Long = financialProductOptionId
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_rate_type", length = 50)
    var interestRateType: InterestRateType = interestRateType
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_type", length = 50)
    var reserveType: ReserveType? = reserveType
        protected set

    @Column(name = "deposit_period_months", columnDefinition = "smallint")
    var depositPeriodMonths: Int = depositPeriodMonths
        protected set

    @Column(name = "base_interest_rate", precision = 8, scale = 5)
    var baseInterestRate: BigDecimal? = baseInterestRate
        protected set

    @Column(name = "maximum_interest_rate", precision = 8, scale = 5)
    var maximumInterestRate: BigDecimal? = maximumInterestRate
        protected set

    @Column(name = "source_payload", columnDefinition = "text")
    var sourcePayload: String? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_product_id")
    var financialProductEntity: FinancialProductEntity = financialProductEntity
        protected set
}
