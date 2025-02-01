package ninja.sundry.financial.adapter.out.persistence.financial.entity

import domain.financial.InterestRateType
import domain.financial.ReserveType
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
import ninja.sundry.financial.adapter.out.persistence.BaseTimeEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "financial_product_option",
    indexes = [
        Index(columnList = "financialProductId"),
    ],
)
class FinancialProductOptionEntity(
    financialProductOptionId: Long = 0L,
    interestRateType: InterestRateType,
    reserveType: ReserveType?,
    depositPeriodMonths: String,
    baseInterestRate: BigDecimal?,
    maximumInterestRate: BigDecimal?,
    financialProductEntity: FinancialProductEntity,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var financialProductOptionId: Long = financialProductOptionId
        protected set

    @Enumerated(EnumType.STRING)
    var interestRateType: InterestRateType = interestRateType
        protected set

    @Enumerated(EnumType.STRING)
    var reserveType: ReserveType? = reserveType
        protected set

    var depositPeriodMonths: String = depositPeriodMonths
        protected set

    @Column(precision = 5, scale = 2)
    var baseInterestRate: BigDecimal? = baseInterestRate
        protected set

    @Column(precision = 5, scale = 2)
    var maximumInterestRate: BigDecimal? = maximumInterestRate
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financialProductId")
    var financialProductEntity: FinancialProductEntity = financialProductEntity
        protected set
}
