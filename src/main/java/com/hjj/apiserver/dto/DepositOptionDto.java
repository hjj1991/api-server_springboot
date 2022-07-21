package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hjj.apiserver.domain.Deposit;
import com.hjj.apiserver.domain.DepositOption;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DepositOptionDto {

    @JsonProperty("intr_rate2")
    private double intrRate2;
    @JsonProperty("intr_rate")
    private double intrRate;
    @JsonProperty("save_trm")
    private String saveTrm;
    @JsonProperty("intr_rate_type_nm")
    private String intrRateTypeNm;
    @JsonProperty("intr_rate_type")
    private String intrRateType;
    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;
    @JsonProperty("fin_co_no")
    private String finCoNo;
    @JsonProperty("dcls_month")
    private String dclsMonth;


    public DepositOption toEntity(Deposit deposit){
        return DepositOption.builder()
                .deposit(deposit)
                .intrRate(intrRate)
                .intrRate2(intrRate2)
                .saveTrm(saveTrm)
                .intrRateTypeNm(intrRateTypeNm)
                .intrRateType(intrRateType)
                .dclsMonth(dclsMonth).build();
    }
}
