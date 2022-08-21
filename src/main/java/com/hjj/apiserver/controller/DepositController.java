package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError_Java;
import com.hjj.apiserver.common.ApiResponse_Java;
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
    public ApiResponse_Java depositList() {
        try {

            return ApiUtils.success(depositService.findDepositList());
        } catch (Exception e) {
            log.error("depositList Error exception: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0005.getMsg(), ApiError_Java.ErrCode.ERR_CODE0005);
        }

    }

}
