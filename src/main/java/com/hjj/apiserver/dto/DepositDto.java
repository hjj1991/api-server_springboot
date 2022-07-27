package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hjj.apiserver.domain.Bank;
import com.hjj.apiserver.domain.Deposit;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DepositDto {

    private String finCoSubmDay;
    private String dclsStrtDay;
    private String dclsEndDay;
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

    private Bank.BankType bankType;
    private BankDto bankDto;

    private List<DepositOptionDto> options;

    public Deposit toEntity(Bank bank){

//        List<DepositOption> depositOptions = options.stream().map(option -> option.toEntity()).collect(Collectors.toList());

        return Deposit.builder()
                .finPrdtCd(finPrdtCd)
                .bank(bank)
                .finCoSubmDay(finCoSubmDay)
                .dclsStrtDay(dclsStrtDay)
                .dclsEndDay(dclsEndDay)
                .maxLimit(maxLimit)
                .etcNote(etcNote)
                .joinMember(joinMember)
                .joinDeny(joinDeny)
                .spclCnd(spclCnd)
                .mtrtInt(mtrtInt)
                .joinWay(joinWay)
                .finPrdtNm(finPrdtNm)
                .korCoNm(korCoNm)
                .dclsMonth(dclsMonth)
                .build();
    }

    @Getter
    @Setter
    public static class Result {
        private List<DepositOptionDto> optionList;
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
    }



    @Data
    public static class ResponseDepositFindAll{
        private String finCoSubmDay;
        private String dclsStrtDay;
        private String dclsEndDay;
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
        private String bankType;
        private String calTel;
        private String hompUrl;
        private String dclsChrgMan;

        private List<Option> options;

        @Data
        public static class Option{

            private double intrRate2;
            private double intrRate;
            private String saveTrm;
            private String intrRateTypeNm;
            private String intrRateType;

        }
    }


    @Data
    public static class DepositIntrRateDesc{

        @QueryProjection
        public DepositIntrRateDesc(String korCoNm, String finPrdtNm, Double intrRate, Double intrRate2) {
            this.korCoNm = korCoNm;
            this.finPrdtNm = finPrdtNm;
            this.intrRate = intrRate;
            this.intrRate2 = intrRate2;
        }

        private String korCoNm;
        private String finPrdtNm;
        private Double intrRate;
        private Double intrRate2;
    }

}
