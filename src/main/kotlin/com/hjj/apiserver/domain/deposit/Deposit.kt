package com.hjj.apiserver.domain.deposit

import com.hjj.apiserver.domain.BaseTimeEntity
import com.hjj.apiserver.domain.bank.Bank
import jakarta.persistence.*

@Entity
@Table(name = "tb_deposit")
@IdClass(DepositPK::class)
class Deposit(
    @Id // 금융상품 코드
    @Column(length = 50)
    val finPrdtCd: String? = null,
    @Id // 금융회사 코드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finCoNo", columnDefinition = "VARCHAR(20)", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val bank: Bank? = null,
    @Column // 최고한도
    val maxLimit: Long? = null,
    @Column(columnDefinition = "varchar(2000)") // 우대조건
    val spclCnd: String? = null,
    @Column(columnDefinition = "varchar(2000)") // 만기 후 이자율
    val mtrtInt: String? = null,
    @Column // 가입대상
    val joinMember: String? = null,
    @Column // 가입방법
    val joinWay: String? = null,
    @Column // 가입제한 EX) 1:제한없음, 2:서민전용, 3일부제한
    val joinDeny: String? = null,
    @Column // 금융회사명
    val korCoNm: String? = null,
    @Column // 금융상품명
    val finPrdtNm: String? = null,
    @Column // 기타 유의사항
    val etcNote: String? = null,
    @Column // 공시 제출일[YYYYMM]
    val dclsMonth: String? = null,
    @Column
    val dclsStrtDay: String? = null,
    @Column
    val dclsEndDay: String? = null,
    @Column // 금융회사 제출일 [YYYYMMDDHH24MI]
    val finCoSubmDay: String? = null,
    @Column
    val enable: Int = 1,
    @OneToMany(mappedBy = "deposit", fetch = FetchType.LAZY)
    val depositOptions: MutableList<DepositOption> = mutableListOf(),
) : BaseTimeEntity()
