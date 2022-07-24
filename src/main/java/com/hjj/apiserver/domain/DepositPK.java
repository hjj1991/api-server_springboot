package com.hjj.apiserver.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class DepositPK implements Serializable {

    private static final long serialVersionUID = -2929789292155268166L;

    @EqualsAndHashCode.Include
    private String bank;    //finCoNo
    @EqualsAndHashCode.Include
    private String finPrdtCd;

    public DepositPK(String finCoNo, String finPrdtCd) {
        bank = finCoNo;
        this.finPrdtCd = finPrdtCd;
    }
}
