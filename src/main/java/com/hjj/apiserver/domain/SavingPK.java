package com.hjj.apiserver.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class SavingPK implements Serializable {


    private static final long serialVersionUID = -4052438448409315836L;

    @EqualsAndHashCode.Include
    private String bank;    //finCoNo
    @EqualsAndHashCode.Include
    private String finPrdtCd;

    public SavingPK(String finCoNo, String finPrdtCd) {
        this.bank = finCoNo;
        this.finPrdtCd = finPrdtCd;
    }
}
