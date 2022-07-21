package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.service.DepositService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"6. Deposit"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;




    @ApiOperation(value = "예금 목록", notes = "예금 목록 조회.")
    @GetMapping("/deposit")
    public ApiResponse depositList() {
        try {

            return ApiUtils.success(depositService.findDepositListByBankType());
        } catch (Exception e) {
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

}
