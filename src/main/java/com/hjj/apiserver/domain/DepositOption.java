package com.hjj.apiserver.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(DepositOptionPK.class)
@Table(name = "tb_deposit_option")
public class DepositOption extends BaseTimeEntity {
    @Id
    @Column //저축 기간[단위: 개월]
    private String saveTrm;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "finPrdtCd",
                    referencedColumnName = "finPrdtCd"),
            @JoinColumn(name = "finCoNo",
                    referencedColumnName = "finCoNo")
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Deposit deposit;



    @Column //저축 금리 [소수점 2자리]
    private double intrRate2;
    @Column //최고 우대금리[소수점 2자리]
    private double intrRate;
    @Column //저축 금리 유형명
    private String intrRateTypeNm;
    @Column //저축 금리 유형
    private String intrRateType;
    @Column
    private String dclsMonth;
    @Column
    private int enable;


}
