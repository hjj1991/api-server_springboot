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
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class PurchaseController(
    private val purchaseService: PurchaseService,
) {

    @PostMapping("/purchase")
    fun purchaseAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestBody @Valid request: PurchaseAddRequest
    ): PurchaseAddResponse {
        request.validRequest()
        return purchaseService.addPurchase(currentUserInfo.userNo, request)
    }

    @GetMapping("/purchase")
    fun purchasesFind(
        @CurrentUser user: CurrentUserInfo,
        request: PurchaseFindOfPageRequest
    ): Slice<PurchaseFindOfPageResponse> {
        return purchaseService.findPurchasesOfPage(
            request,
            request.getPageRequest()
        )
    }

    @DeleteMapping("/purchase/{purchaseNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun purchaseRemove(@CurrentUser currentUserInfo: CurrentUserInfo, @PathVariable("purchaseNo") purchaseNo: Long) {
        purchaseService.removePurchase(currentUserInfo.userNo, purchaseNo)
    }


    @GetMapping("/purchase/{purchaseNo}")
    fun purchaseDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("purchaseNo") purchaseNo: Long
    ): PurchaseDetailResponse {
        return purchaseService.findPurchase(currentUserInfo.userNo, purchaseNo)
    }

    @PatchMapping("/purchase/{purchaseNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun purchaseModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("purchaseNo") purchaseNo: Long,
        @RequestBody @Valid request: PurchaseModifyRequest
    ) {
        request.validRequest()
        purchaseService.modifyPurchase(currentUserInfo.userNo, purchaseNo, request)
    }
}