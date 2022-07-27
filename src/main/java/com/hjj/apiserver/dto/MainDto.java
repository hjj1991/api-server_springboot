package com.hjj.apiserver.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MainDto {


    @Data
    @Builder
    public static class ResponseMain implements Serializable {

        List<DepositDto.DepositIntrRateDesc> deposits;
        List<SavingDto.SavingIntrRateDesc> savings;
    }
}
