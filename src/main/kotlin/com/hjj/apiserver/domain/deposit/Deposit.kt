package com.hjj.apiserver.domain.deposit

import com.hjj.apiserver.domain.BaseTimeEntity
import com.hjj.apiserver.domain.bank.Bank
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "tb_deposit")
@IdClass(DepositPK::class)
class Deposit(
    @Id // 금융상품 코드
    @Column(length = 50)
    val finPrdtCd: String? = null,
    @Id // 금융회사 코드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "finCoNo",
        columnDefinition = "VARCHAR(20)",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    )
    val bank: Bank? = null,
    // 최고한도
    @Column
    val maxLimit: Long? = null,
    // 우대조건
    @Column(columnDefinition = "varchar(2000)")
    val spclCnd: String? = null,
    // 만기 후 이자율
    @Column(columnDefinition = "varchar(2000)")
    val mtrtInt: String? = null,
    // 가입대상
    @Column
    val joinMember: String? = null,
    // 가입방법
    @Column
    val joinWay: String? = null,
    // 가입제한 EX) 1:제한없음, 2:서민전용, 3일부제한
    @Column
    val joinDeny: String? = null,
    // 금융회사명
    @Column
    val korCoNm: String? = null,
    // 금융상품명
    @Column
    val finPrdtNm: String? = null,
    // 기타 유의사항
    @Column
    val etcNote: String? = null,
    // 공시 제출일[YYYYMM]
    @Column
    val dclsMonth: String? = null,
    @Column
    val dclsStrtDay: String? = null,
    @Column
    val dclsEndDay: String? = null,
    // 금융회사 제출일 [YYYYMMDDHH24MI]
    @Column
    val finCoSubmDay: String? = null,
    @Column
    val enable: Int = 1,
    @OneToMany(mappedBy = "deposit", fetch = FetchType.LAZY)
    val depositOptions: MutableList<DepositOption> = mutableListOf(),
) : BaseTimeEntity()
