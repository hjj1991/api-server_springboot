package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseAddResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseFindOfPageResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.impl.PurchaseService
import com.hjj.apiserver.util.AuthUser
import jakarta.validation.Valid
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class PurchaseController(
    private val purchaseService: PurchaseService,
) {

    @PostMapping("/purchase")
    fun purchaseAdd(
        @AuthUser authUserInfo: CurrentUserInfo,
        @RequestBody @Valid request: PurchaseAddRequest
    ): PurchaseAddResponse {
        request.validRequest()
        return purchaseService.addPurchase(authUserInfo.userNo, request)
    }

    @GetMapping("/purchase")
    fun purchasesFind(
        @AuthUser user: CurrentUserInfo,
        request: PurchaseFindOfPageRequest
    ): Slice<PurchaseFindOfPageResponse> {
        return purchaseService.findPurchasesOfPage(
            request,
            request.getPageRequest()
        )
    }

    @DeleteMapping("/purchase/{purchaseNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun purchaseRemove(@AuthUser authUserInfo: CurrentUserInfo, @PathVariable("purchaseNo") purchaseNo: Long) {
        purchaseService.removePurchase(authUserInfo.userNo, purchaseNo)
    }


    @GetMapping("/purchase/{purchaseNo}")
    fun purchaseDetail(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("purchaseNo") purchaseNo: Long
    ): PurchaseDetailResponse {
        return purchaseService.findPurchase(authUserInfo.userNo, purchaseNo)
    }

    @PatchMapping("/purchase/{purchaseNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun purchaseModify(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("purchaseNo") purchaseNo: Long,
        @RequestBody @Valid request: PurchaseModifyRequest
    ) {
        request.validRequest()
        purchaseService.modifyPurchase(authUserInfo.userNo, purchaseNo, request)
    }
}