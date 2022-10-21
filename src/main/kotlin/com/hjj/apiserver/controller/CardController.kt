package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.CardService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*

@Api(tags = ["2. Card"])
@RestController
class CardController(
    private val cardService: CardService,
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
    @ApiOperation(value = "개인 카드 목록 조회", notes = "개인카드 목록조회한다..")
    @GetMapping("/card")
    fun cardsFind(@CurrentUser currentUserInfo: CurrentUserInfo): ApiResponse<List<CardFindAllResponse>> {
        return ApiUtils.success(cardService.findCards(currentUserInfo.userNo))
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
    @ApiOperation(value = "개인 카드 등록", notes = "개인카드 등록한다.")
    @PostMapping("/card")
    fun cardAdd(@CurrentUser currentUserInfo: CurrentUserInfo, @RequestBody request: CardAddRequest): ApiResponse<*> {
        cardService.findCard(currentUserInfo.userNo, request)
        return ApiUtils.success()
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
    @ApiOperation(httpMethod = "DELETE", value = "개인 카드 삭제", notes = "개인 카드를 삭제한다.", responseContainer = "Integer")
    @DeleteMapping("/card/{cardNo}")
    fun cardRemove(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long
    ): ApiResponse<*> {
        cardService.removeCard(currentUserInfo.userNo, cardNo)
        return ApiUtils.success()
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
    @ApiOperation(httpMethod = "PUT", value = "개인 카드 수정", notes = "개인 카드를 삭제한다.", responseContainer = "Integer")
    @PutMapping("/card/{cardNo}")
    fun cardModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long,
        @RequestBody request: CardModifyRequest
    ): ApiResponse<*> {
        cardService.modifyCard(currentUserInfo.userNo, cardNo, request)
        return ApiUtils.success()
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
    @ApiOperation(httpMethod = "GET", value = "개인 카드 상세", notes = "개인 카드 상세확인페이지", responseContainer = "Integer")
    @GetMapping("/card/{cardNo}")
    fun cardDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long
    ): ApiResponse<CardFindResponse> {
        return ApiUtils.success(cardService.findCard(currentUserInfo.userNo, cardNo))
    }

}