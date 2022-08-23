package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.request.UserSinUpRequest
import com.hjj.apiserver.service.user.UserService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.logger
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

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

    @ApiOperation(value = "유저 회원가입", notes = "유저 회원가입을 한다.")
    @PostMapping("/user/signup")
    fun signUp(@Valid @RequestBody @ApiParam(value = "회원 한 명의 정보를 갖는 객체", required = true) request: UserSinUpRequest): ApiResponse<*> {
        return try{
            userService.signUp(request)
            ApiUtils.success()
        }catch (e: Exception){
            log.error("[signUp] Error Request: {}, ErrorInfo: {}", request, e.printStackTrace())
            ApiUtils.error(ErrCode.ERR_CODE0004)
        }
    }

    @ApiOperation(value = "유저 로그인", notes = "유저 로그인을 한다.")
    @PostMapping("/user/signin")
    fun signIn(@RequestBody request: UserSignInRequest): ApiResponse<*>  {
        return try{
            ApiUtils.success(userService.signIn(request))
        }catch (e: ExistedSocialUserException){
            ApiUtils.error(ErrCode.ERR_CODE0007)
        }catch (e: BadCredentialsException){
            ApiUtils.error(ErrCode.ERR_CODE0008)
        }catch (e: Exception){
            log.error("[signIn] Error Request: {}, ErrorInfo: {}", request, e.printStackTrace())
            ApiUtils.error(ErrCode.ERR_CODE9999)
        }
    }


}