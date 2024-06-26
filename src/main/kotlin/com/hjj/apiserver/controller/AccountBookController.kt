package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookAddResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.impl.AccountBookService
import com.hjj.apiserver.util.AuthUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountBookController(
    private val accountBookService: AccountBookService,
) {
    @PostMapping("/account-books")
    @ResponseStatus(HttpStatus.CREATED)
    fun accountBookAdd(
        @AuthUser authUserInfo: CurrentUserInfo,
        @Valid @RequestBody request: AccountBookAddRequest,
    ): AccountBookAddResponse {
        return accountBookService.addAccountBook(authUserInfo.userNo, request)
    }

    @GetMapping("/account-books")
    fun accountBooksFind(
        @AuthUser user: CurrentUserInfo,
    ): List<AccountBookFindAllResponse> {
        return accountBookService.findAllAccountBook(user.userNo)
    }

    @GetMapping("/account-books/{accountBookNo}")
    fun accountBookDetail(
        @AuthUser user: CurrentUserInfo,
        @PathVariable accountBookNo: Long,
    ): AccountBookDetailResponse {
        val findAccountBookDetail = accountBookService.findAccountBookDetail(accountBookNo, user.userNo)
        return findAccountBookDetail
    }
}
