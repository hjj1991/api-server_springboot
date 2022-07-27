package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/main")
    public ApiResponse main(){
        try{
            return ApiUtils.success(mainService.findMain());
        } catch (Exception e) {
            log.error("main Error exception: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }
    }
}
