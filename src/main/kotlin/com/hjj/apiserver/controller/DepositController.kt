package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.deposit.response.DepositFindAllResponse
import com.hjj.apiserver.service.impl.DepositService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["6. Deposit"])
@RestController
class DepositController(
    private val depositService: DepositService,
) {
    @ApiOperation(value = "예금 목록", notes = "예금 목록 조회.")
    @GetMapping("/deposit")
    fun depositsFind(): List<DepositFindAllResponse> {
        return depositService.findDeposits()
    }
}
