package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.service.SavingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"7. Saving"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class SavingController {

    private final SavingService savingService;




    @ApiOperation(value = "적금 목록", notes = "적금 목록 조회.")
    @GetMapping("/saving")
    public ApiResponse savingList() {
        try {

            return ApiUtils.success(savingService.findSavingList());
        } catch (Exception e) {
            log.error("savingList Error exception: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }
}
