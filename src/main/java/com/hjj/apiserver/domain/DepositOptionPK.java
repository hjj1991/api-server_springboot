package com.hjj.apiserver.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DepositOptionPK implements Serializable {


    private static final long serialVersionUID = -7401385908325824686L;


    @EqualsAndHashCode.Include
    private String saveTrm; //저축 기간[단위: 개월]

    @EqualsAndHashCode.Include
    private Deposit deposit;

}
