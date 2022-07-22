package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hjj.apiserver.domain.Bank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
public class SavingDto implements Serializable {
    private static final long serialVersionUID = -8289104489574860595L;


    private String finCoSubmDay;
    private String dclsStrtDay;
    private long maxLimit;
    private String etcNote;
    private String joinMember;
    private String joinDeny;
    private String spclCnd;
    private String mtrtInt;
    private String joinWay;
    private String finPrdtNm;
    private String korCoNm;
    private String finPrdtCd;
    private String finCoNo;
    private String dclsMonth;
    private String dclsEndDay;
    private Bank.BankType bankType;
    private BankDto bankDto;

    private List<SavingOptionDto> options;

    @Getter
    @Setter
    public static class Result {
        private List<SavingOptionDto> optionList;
        private List<Baselist> baseList;
        @JsonProperty("err_msg")
        private String errMsg;
        @JsonProperty("err_cd")
        private String errCd;
        @JsonProperty("now_page_no")
        private String nowPageNo;
        @JsonProperty("max_page_no")
        private String maxPageNo;
        @JsonProperty("total_count")
        private String totalCount;
        @JsonProperty("prdt_div")
        private String prdtDiv;
    }

    @Getter
    @Setter
    public static class Baselist {
        @JsonProperty("fin_co_subm_day")
        private String finCoSubmDay;
        @JsonProperty("dcls_strt_day")
        private String dclsStrtDay;
        @JsonProperty("dcls_end_day")
        private String dclsEndDay;
        @JsonProperty("max_limit")
        private long maxLimit;
        @JsonProperty("etc_note")
        private String etcNote;
        @JsonProperty("join_member")
        private String joinMember;
        @JsonProperty("join_deny")
        private String joinDeny;
        @JsonProperty("spcl_cnd")
        private String spclCnd;
        @JsonProperty("mtrt_int")
        private String mtrtInt;
        @JsonProperty("join_way")
        private String joinWay;
        @JsonProperty("fin_prdt_nm")
        private String finPrdtNm;
        @JsonProperty("kor_co_nm")
        private String korCoNm;
        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;
        @JsonProperty("fin_co_no")
        private String finCoNo;
        @JsonProperty("dcls_month")
        private String dclsMonth;

        public boolean isSavingOption(SavingOptionDto savingOptionDto){
            if(finCoNo.equals(savingOptionDto.getFinCoNo()) && finPrdtCd.equals(savingOptionDto.getFinPrdtCd())){
                return true;
            }
            return false;
        }
    }

    @Data
    public static class ResponseSavingFindAll{
        private String finCoSubmDay;
        private String dclsStrtDay;
        private long maxLimit;
        private String etcNote;
        private String joinMember;
        private String joinDeny;
        private String spclCnd;
        private String mtrtInt;
        private String joinWay;
        private String finPrdtNm;
        private String korCoNm;
        private String finPrdtCd;
        private String finCoNo;
        private String dclsMonth;
        private String dclsEndDay;
        private Bank.BankType bankType;
        private String calTel;
        private String hompUrl;
        private String dclsChrgMan;

        private List<ResponseSavingFindAll.Option> options;

        @Data
        public static class Option{

            private double intrRate2;
            private double intrRate;
            private String saveTrm;
            private String rsrvTypeNm;
            private String rsrvType;
            private String intrRateTypeNm;
            private String intrRateType;
            private String dclsMonth;

        }
    }

}
