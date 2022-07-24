package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SavingOptionDto {

    @JsonProperty("intr_rate2")
    private double intrRate2;
    @JsonProperty("intr_rate")
    private double intrRate;
    @JsonProperty("save_trm")
    private String saveTrm;
    @JsonProperty("rsrv_type_nm")
    private String rsrvTypeNm;
    @JsonProperty("rsrv_type")
    private String rsrvType;
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

}
