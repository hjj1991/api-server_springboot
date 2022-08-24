package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.request.ReIssueTokenRequest
import com.hjj.apiserver.dto.user.request.UserModifyRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.request.UserSinUpRequest
import com.hjj.apiserver.service.FireBaseService
import com.hjj.apiserver.service.UserService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.CurrentUser
import com.hjj.apiserver.util.logger
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@Api(tags = ["1. User"])
@RestController
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fireBaseService: FireBaseService,
) {
    private val log = logger()

    @ApiOperation(value = "유저Id 중복 조회", notes = "유저id의 중복여부를 확인한다.")
    @GetMapping("/user/{userId}/exists-id")
    fun checkUserIdDuplicate(@PathVariable userId: String): ApiResponse<*> {
        return if(userService.existsUserId(userId)){
                ApiUtils.error(ErrCode.ERR_CODE0002)
            }else{
                ApiUtils.success()
            }
    }

    @ApiOperation(value = "유저 회원가입", notes = "유저 회원가입을 한다.")
    @PostMapping("/user/signup")
    fun signUp(@Valid @RequestBody @ApiParam(value = "회원 한 명의 정보를 갖는 객체", required = true) request: UserSinUpRequest): ApiResponse<*> {
        userService.signUp(request)
        return ApiUtils.success()

    }

    @ApiOperation(value = "유저 로그인", notes = "유저 로그인을 한다.")
    @PostMapping("/user/signin")
    fun signIn(@RequestBody request: UserSignInRequest): ApiResponse<*>  {
        return ApiUtils.success(userService.signIn(request))
    }

    @ApiOperation(value = "소셜유저 로그인", notes = "소셜유저 로그인을 한다.")
    @PostMapping("/user/social/signin")
    fun socialSignIn(@RequestBody request: HashMap<String, String>): ApiResponse<*> {
        if(!Provider.isExist(request["provider"])){
            return ApiUtils.error(ErrCode.ERR_CODE0009)
        }
        return ApiUtils.success(userService.socialSignIn(request))
    }

    @ApiOperation(value = "소셜유저 회원가입", notes = "소셜유저 회원가입을 한다.")
    @PutMapping("/user/social/signup")
    fun socialSignUp(@RequestBody request: HashMap<String, String>): ApiResponse<*> {
        if(!Provider.isExist(request["provider"])){
            return ApiUtils.error(ErrCode.ERR_CODE0009)
        }

        userService.socialSignUp(request)
        return ApiUtils.success()
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
    @ApiOperation(value = "기존 유저를 소셜계정 연동", notes = "기존 유저를 소셜유게정 연동 한다.")
    @PatchMapping("/user/social/mapping")
    fun socialMapping(@CurrentUser user:User, @RequestBody request:HashMap<String, String>): ApiResponse<*> {
        if(!Provider.isExist(request["provider"])){
            return ApiUtils.error(ErrCode.ERR_CODE0009)
        }
        userService.socialMapping(user, request)
        return ApiUtils.success()
    }

    @ApiOperation(value = "AcessToken 재발급", notes = "AcessToken을 재발급한다.")
    @PostMapping("/user/oauth/token")
    fun reIssueToken(@RequestBody request: ReIssueTokenRequest): ApiResponse<*>{

        return ApiUtils.success(userService.reIssueToken(request.refreshToken))

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
    @ApiOperation(value = "유저정보 상세조회", notes = "유저 정보를 상세 조회한다.")
    @GetMapping("/user")
    fun userDetails(@CurrentUser user: User): ApiResponse<*>{
        return ApiUtils.success()
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
    @ApiOperation(value = "유저정보 업데이트", notes = "유저 정보를 업데이트한다.")
    @PatchMapping("/user")
    fun userModify(@CurrentUser user: User, @RequestBody @Valid request: UserModifyRequest): ApiResponse<*>{
        return ApiUtils.success(userService.modifyUser(user, request))
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
    @ApiOperation(value = "유저프로필 사진 업데이트", notes = "유저 프로필사진을 업데이트한다.")
    @PatchMapping("/user/profile")
    fun userProfileImgModify(@CurrentUser user: User, pictureFile: MultipartFile): ApiResponse<*>{

        userService.modifyUserPicture(user, pictureFile)
        return ApiUtils.success()

    }

    @GetMapping(value = ["/user/profile"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun userProfileImgDetails(Authorization: String, picture: String ): ResponseEntity<ByteArray> {
        if(StringUtils.hasText(Authorization) && jwtTokenProvider.validateToken(Authorization)){
            return ResponseEntity(fireBaseService.getProfileImg(picture), HttpStatus.OK)
        }

        return ResponseEntity(HttpStatus.NOT_FOUND)
    }


}