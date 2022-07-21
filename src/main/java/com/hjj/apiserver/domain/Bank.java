package com.hjj.apiserver.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_bank")
public class Bank extends BaseTimeEntity {

    @RequiredArgsConstructor
    public enum BankType {
        BANK(020000, "은행"),
        SAVING_BANK(030300, "저축은행");

        private final int topFinGrpNo;
        private final String title;
    }

    @Id
    private String finCoNo;

    @Column
    private String dclsMonth;
    @Column
    private String korCoNm;
    @Column
    private String dclsChrgMan;
    @Column
    private String hompUrl;
    @Column
    private String calTel;

    @Column
    @Enumerated(EnumType.STRING)
    private BankType bankType;

    @Column
    private int enable;


    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Deposit> deposits = new ArrayList<>();

}
