package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.service.MainService
import com.hjj.apiserver.util.ApiUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController(
    private val mainService: MainService,
) {

    @GetMapping("/main")
    fun main():ApiResponse<*>{
        return ApiUtils.success(mainService.findMain())
    }
}