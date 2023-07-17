package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseAddResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseFindOfPageResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.PurchaseService
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.web.bind.annotation.*

@RestController
class PurchaseController(
    private val purchaseService: PurchaseService,
) {

    @PostMapping("/purchase")
    fun purchaseAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestBody request: PurchaseAddRequest
    ): PurchaseAddResponse {
        request.validRequest()
        return purchaseService.addPurchase(currentUserInfo.userNo, request)
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "지출,수입 리스트", notes = "지출, 수입을 리스트를 불러온다.")
    @GetMapping("/purchase")
    fun purchasesFind(
        @CurrentUser user: CurrentUserInfo,
        request: PurchaseFindOfPageRequest
    ): Slice<PurchaseFindOfPageResponse> {
        return purchaseService.findPurchasesOfPage(
            request,
            PageRequest.of(request.page, request.size)
        )
    }


    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "지출,수입 삭제", notes = "지출, 수입을 삭제한다.")
    @DeleteMapping("/purchase/{purchaseNo}")
    fun purchaseRemove(@CurrentUser currentUserInfo: CurrentUserInfo, @PathVariable("purchaseNo") purchaseNo: Long) {
        purchaseService.removePurchase(currentUserInfo.userNo, purchaseNo)
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "지출, 수입 상세조회", notes = "지출, 수입을 상세 조회한다.")
    @GetMapping("/purchase/{purchaseNo}")
    fun purchaseDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("purchaseNo") purchaseNo: Long
    ): PurchaseDetailResponse {
        return purchaseService.findPurchase(currentUserInfo.userNo, purchaseNo)
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "지출, 수입 수정", notes = "지출, 수입을 수정한다.")
    @PatchMapping("/purchase/{purchaseNo}")
    fun purchaseModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "purchaseNo", required = true) @PathVariable("purchaseNo") purchaseNo: Long,
        @RequestBody request: PurchaseModifyRequest
    ) {
        request.validRequest()
        purchaseService.modifyPurchase(currentUserInfo.userNo, purchaseNo, request)
    }
}