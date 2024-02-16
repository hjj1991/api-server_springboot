package com.hjj.apiserver.domain.bank

import com.hjj.apiserver.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "tb_bank")
class Bank(
    dclsMonth: String? = null,
    korCoNm: String? = null,
    dclsChrgMan: String? = null,
    hompUrl: String? = null,
    calTel: String? = null,
    bankType: BankType,
    enable: Int = 1,
) : BaseTimeEntity() {
    @Id
    @Column(length = 20)
    val finCoNo: String? = null

    @Column(length = 20)
    var dclsMonth: String? = dclsMonth
        protected set

    @Column(length = 100)
    var korCoNm: String? = korCoNm
        protected set

    @Column
    var dclsChrgMan: String? = dclsChrgMan
        protected set

    @Column
    var hompUrl: String? = hompUrl
        protected set

    @Column(length = 50)
    var calTel: String? = calTel
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var bankType: BankType = bankType
        protected set

    @Column
    var enable: Int = enable
        protected set
}
