package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.repository.user.UserRepository
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
            paramType = "header"
        )
    )
    @ApiOperation(value = "개인 카드 목록 조회", notes = "개인카드 목록조회한다..")
    @GetMapping("/card")
    fun cardFindList(@CurrentUser user:User): ApiResponse<*> {
        return ApiUtils.success(cardService.selectCards(user.userNo!!))
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            paramType = "header"
        )
    )
    @ApiOperation(value = "개인 카드 등록", notes = "개인카드 등록한다.")
    @PostMapping("/card")
    fun cardAdd(@CurrentUser user: User, @RequestBody request: CardAddRequest){
        ApiUtils.success(cardService.insertCard(user, request))
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            paramType = "header"
        )
    )
    @ApiOperation(httpMethod = "DELETE", value = "개인 카드 삭제", notes = "개인 카드를 삭제한다.", responseContainer = "Integer")
    @DeleteMapping("/card/{cardNo}")
    fun cardRemove(@CurrentUser user: User, @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long){
        cardService.deleteCard(user.userNo!!, cardNo)
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            paramType = "header"
        )
    )
    @ApiOperation(httpMethod = "PUT", value = "개인 카드 수정", notes = "개인 카드를 삭제한다.", responseContainer = "Integer")
    @PutMapping("/card/{cardNo}")
    fun cardModify(@CurrentUser user: User, @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long, @RequestBody request: CardModifyRequest){
        cardService.updateCard(user.userNo!!, cardNo, request)
        ApiUtils.success()
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            paramType = "header"
        )
    )
    @ApiOperation(httpMethod = "GET", value = "개인 카드 상세", notes = "개인 카드 상세확인페이지", responseContainer = "Integer")
    @GetMapping("/card/{cardNo}")
    fun cardDetails(@CurrentUser user: User, @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") cardNo: Long): ApiResponse<*>{
        return ApiUtils.success(cardService.selectCard(user.userNo!!, cardNo))
    }

}