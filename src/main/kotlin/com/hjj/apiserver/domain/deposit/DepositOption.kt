package com.hjj.apiserver.domain.deposit

import com.hjj.apiserver.domain.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tb_deposit_option")
class DepositOption(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val depositOptionNo: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        value = [
            JoinColumn(
                name = "finPrdtCd",
                referencedColumnName = "finPrdtCd",
            ),
            JoinColumn(
                name = "finCoNo",
                referencedColumnName = "finCoNo",
            ),
        ],
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    )
    val deposit: Deposit? = null,
    // 저축 금리 [소수점 2자리]
    @Column
    val intrRate2: Double? = null,
    // 최고 우대금리[소수점 2자리]
    @Column
    val intrRate: Double? = null,
    // 저축 금리 유형명
    @Column(length = 20)
    val intrRateTypeNm: String? = null,
    // 저축 기간[단위: 개월]
    @Column(length = 10)
    val saveTrm: String? = null,
    // 저축 금리 유형
    @Column(length = 4)
    val intrRateType: String? = null,
    @Column
    val dclsMonth: String? = null,
) : BaseTimeEntity()
