package com.hjj.apiserver.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tb_deposit")
@IdClass(DepositPK.class)
public class Deposit extends BaseTimeEntity_Java {



    @Id//금융상품코드
    @Column(length = 50)
    private String finPrdtCd;

    @Id//금융회사코드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="finCoNo", columnDefinition="VARCHAR(20)", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Bank bank;

    @Column//최고한도
    private Long maxLimit;
    @Column(columnDefinition = "varchar(2000)")//우대조건
    private String spclCnd;
    @Column(columnDefinition = "varchar(2000)")//만기 후 이자율
    private String mtrtInt;
    @Column//가입대상
    private String joinMember;
    @Column//가입방법
    private String joinWay;
    @Column//가입제한 EX) 1:제한없음, 2:서민전용, 3일부제한
    private String joinDeny;
    @Column//금융회사명
    private String korCoNm;
    @Column//금융상품명
    private String finPrdtNm;
    @Column//기타 유의사항
    private String etcNote;
    @Column//공시 제출일[YYYYMM]
    private String dclsMonth;
    @Column
    private String dclsStrtDay;
    @Column
    private String dclsEndDay;
    @Column//금융회사 제출일 [YYYYMMDDHH24MI]
    private String finCoSubmDay;

    @Column
    private int enable;

    @OneToMany(mappedBy = "deposit", fetch = FetchType.LAZY)
    @Builder.Default
    private List<DepositOption> depositOptions = new ArrayList<>();


}
