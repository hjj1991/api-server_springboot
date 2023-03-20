package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookAddResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.AccountBookService
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class AccountBookController(
    private val accountBookService: AccountBookService,
) {

    @PostMapping("/account-book")
    fun accountBookAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @Valid @RequestBody request: AccountBookAddRequest
    ): AccountBookAddResponse {
        return accountBookService.addAccountBook(currentUserInfo.userNo, request)
    }

    @GetMapping("/account-book")
    fun accountBooksFind(@CurrentUser user: CurrentUserInfo): List<AccountBookFindAllResponse> {
        return accountBookService.findAllAccountBook(user.userNo)
    }


    @GetMapping("/account-book/{accountBookNo}")
    fun accountBookDetail(
        @CurrentUser user: CurrentUserInfo,
        @PathVariable accountBookNo: Long
    ) {
        accountBookService.findAccountBookDetail(accountBookNo, user.userNo)
    }


}