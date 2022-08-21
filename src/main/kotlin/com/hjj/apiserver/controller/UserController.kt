package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiError
import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.service.user.UserService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.logger
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["1. User"])
@RestController
class UserController(
    private val userService: UserService,
) {
    private val log = logger()

    @ApiOperation(value = "유저Id 중복 조회", notes = "유저id의 중복여부를 확인한다.")
    @GetMapping("/user/{userId}/exists-id")
    fun checkUserIdDuplicate(@PathVariable userId: String): ApiResponse<*> {
        return try{
            if(userService.existsUserId(userId)){
                ApiUtils.error(ErrCode.ERR_CODE0002)
            }else{
                ApiUtils.success()
            }
        }catch (e: Exception){
            log.error("[checkUserIdDuplicate] Error Request: {} , ErrorInfo: {}", userId, e.printStackTrace())
            ApiUtils.error(ErrCode.ERR_CODE9999)
        }
    }


}