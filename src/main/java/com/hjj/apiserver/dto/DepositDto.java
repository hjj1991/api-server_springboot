package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hjj.apiserver.domain.Bank;
import com.hjj.apiserver.domain.Deposit;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DepositDto {

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
    private Bank.BankType bankType;
    private BankDto bankDto;

    private List<DepositOptionDto> options;

    public Deposit toEntity(Bank bank){

//        List<DepositOption> depositOptions = options.stream().map(option -> option.toEntity()).collect(Collectors.toList());

        return Deposit.builder()
                .finPrdtCd(finPrdtCd)
                .bank(bank)
                .finCoSubmDay(finCoSubmDay)
                .dclsStartDay(dclsStrtDay)
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
    public static class ResponseDepositApi {
        private Result result;


        public boolean requestSuccess(){
            if(result != null && result.getErrCd().equals("000")){
                return true;
            }

            return false;
        }

        public boolean isOverLastPage(){
            if(requestSuccess() && Integer.parseInt(result.getMaxPageNo()) < Integer.parseInt(result.getNowPageNo())){
                return true;
            }

            return false;
        }
    }


    @Data
    public static class ResponseDepositFindAll{
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
        private Bank.BankType bankType;
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

}
