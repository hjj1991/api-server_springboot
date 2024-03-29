package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.dto.saving.response.SavingFindAllResponse
import com.hjj.apiserver.service.SavingService
import com.hjj.apiserver.util.ApiUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["7. Saving"])
@RestController
class SavingController(
    private val savingService: SavingService,
) {

    @ApiOperation(value = "적금 목록", notes = "적금 목록 조회.")
    @GetMapping("/saving")
    fun savingsFind(): ApiResponse<List<SavingFindAllResponse>> {
        return ApiUtils.success(savingService.findSavings())
    }
}