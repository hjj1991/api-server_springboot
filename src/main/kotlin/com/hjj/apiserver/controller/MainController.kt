package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.dto.main.response.MainFindResponse
import com.hjj.apiserver.service.MainService
import com.hjj.apiserver.util.ApiUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["8. Main"])
@RestController
class MainController(
    private val mainService: MainService,
) {


    @ApiOperation(value = "메인 화면 조회", notes = "메인에서 보여줄 데이터를 조회한다.")
    @GetMapping("/main")
    fun main():ApiResponse<MainFindResponse>{
        return ApiUtils.success(mainService.findMain())
    }
}