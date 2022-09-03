package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.AccountBookService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(tags = ["5. AccountBook"])
@RestController
class AccountBookController(
    private val accountBookService: AccountBookService,
) {

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
    @ApiOperation(value = "가계부 생성", notes = "가계부를 생성 한다.")
    @PostMapping("/account-book")
    fun accountBookAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @Valid @RequestBody @ApiParam(
            value = "가계부 생성 객체",
            required = true
        ) request: AccountBookAddRequest
    ): ApiResponse<*> {
        return ApiUtils.success(accountBookService.addAccountBook(currentUserInfo.userNo, request))
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
    @ApiOperation(value = "개인가계부 목록을 조회.", notes = "개인가계부를 조회 한다.")
    @GetMapping("/account-book")
    fun accountBooksFind(@CurrentUser user: CurrentUserInfo): ApiResponse<*>{
        return ApiUtils.success(accountBookService.findAllAccountBook(user.userNo))
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
    @ApiOperation(value = "개인가계부 상세 조회.", notes = "개인가계부를 상세 조회 한다.")
    @GetMapping("/account-book/{accountBookNo}")
    fun accountBookDetail(@CurrentUser user: CurrentUserInfo, @PathVariable accountBookNo: Long):ApiResponse<*> {
        return ApiUtils.success(accountBookService.findAccountBookDetail(accountBookNo, user.userNo))
    }



}