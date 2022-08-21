package com.hjj.apiserver.domain.saving

import com.hjj.apiserver.domain.BaseTimeEntity
import com.hjj.apiserver.domain.Saving
import javax.persistence.*

@Entity
@Table(name = "tb_saving_option")
class SavingOption(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var savingOptionNo: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        value = [
            JoinColumn(
                name = "finPrdtCd", referencedColumnName = "finPrdtCd"
            ),
            JoinColumn(
                name = "finCoNo",
                referencedColumnName = "finCoNo"
            )
                ],
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val saving: Saving? = null,


    @Column //저축 금리 [소수점 2자리]
    val intrRate2:Double? = null,

    @Column //최고 우대금리[소수점 2자리]
    val intrRate:Double? = null,

    @Column(length = 20) //저축 금리 유형명
    val intrRateTypeNm: String? = null,

    @Column(length = 10) //저축 기간[단위: 개월]
    val saveTrm: String? = null,

    @Column(length = 4) //저축 금리 유형
    val intrRateType: String? = null,

    @Column
    val dclsMonth: String? = null,

    @Column(length = 10)
    val rsrvType: String? = null,

    @Column(length = 100)
    val rsrvTypeNm: String? = null,


) : BaseTimeEntity() {
}