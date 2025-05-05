package com.hjj.apiserver.adapter.input.web.user

import com.hjj.apiserver.adapter.input.web.user.request.ReIssueTokenRequest
import com.hjj.apiserver.adapter.input.web.user.request.UserSignInRequest
import com.hjj.apiserver.adapter.input.web.user.request.UserSignUpRequest
import com.hjj.apiserver.adapter.input.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.adapter.input.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.input.user.GetUserUseCase
import com.hjj.apiserver.application.port.input.user.WriteUserUseCase
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import mu.two.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val writeUserUseCase: WriteUserUseCase,
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/users/nicknames/{nickName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun checkUserNickNameDuplicate(
        @PathVariable("nickName")
        @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
            message = "닉네임은 공백 제외 한글·영문·숫자 2~10자여야 합니다."
        )
        nickName: String,
    ) {
        getUserUseCase.existsUserNickName(nickName)
    }

//    @GetMapping("/users/exists-id/{userId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    fun checkUserIdDuplicate(
//        @PathVariable("userId") userId: String,
//    ) {
//        if (userCredentialUseCase.existsUserId(userId)) {
//            throw DuplicatedUserIdException()
//        }
//    }

    @PostMapping("/users/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @Valid @RequestBody userSignUpRequest: UserSignUpRequest,
    ) {
        writeUserUseCase.signUp(userSignUpRequest)
    }

    @PostMapping("/users/sign-in")
    fun signIn(
        @Valid @RequestBody userSignInRequest: UserSignInRequest,
        @RequestHeader(value = "User-Agent", required = false) userAgent: String?
    ): UserSignInResponse {
        return writeUserUseCase.signIn(userSignInRequest, userAgent)
    }

    @PostMapping("/users/oauth/reissue-token")
    fun reIssueToken(
        @RequestBody request: ReIssueTokenRequest,
    ): UserReIssueTokenResponse {
        return writeUserUseCase.reissueToken(request.refreshToken)
    }
//
//
//    @ApiImplicitParams(
//        ApiImplicitParam(
//            name = "Authorization",
//            value = "로그인 성공 후 access_token",
//            required = true,
//            dataType = "String",
//            dataTypeClass = String::class,
//            paramType = "header"
//        )
//    )
//    @ApiOperation(value = "기존 유저를 소셜계정 연동", notes = "기존 유저를 소셜유게정 연동 한다.")
//    @PatchMapping("/user/social/mapping")
//    fun socialMapping(
//        @AuthUser authUserInfo: CurrentUserInfo,
//        @RequestBody request: HashMap<String, String>
//    ) {
//
//    }
//

//
//    @ApiImplicitParams(
//        ApiImplicitParam(
//            name = "Authorization",
//            value = "로그인 성공 후 access_token",
//            required = true,
//            dataType = "String",
//            dataTypeClass = String::class,
//            paramType = "header"
//        )
//    )
//    @ApiOperation(value = "유저정보 상세조회", notes = "유저 정보를 상세 조회한다.")
//    @GetMapping("/user")
//    fun userDetail(@AuthUser authUserInfo: CurrentUserInfo): UserDetailResponse? {
//        return userService.findUser(authUserInfo.userNo)
//    }
//
//    @ApiImplicitParams(
//        ApiImplicitParam(
//            name = "Authorization",
//            value = "로그인 성공 후 access_token",
//            required = true,
//            dataType = "String",
//            dataTypeClass = String::class,
//            paramType = "header"
//        )
//    )
//    @ApiOperation(value = "유저정보 업데이트", notes = "유저 정보를 업데이트한다.")
//    @PatchMapping("/user")
//    fun userModify(
//        @AuthUser authUserInfo: CurrentUserInfo,
//        @RequestBody @Valid request: UserModifyRequest
//    ): UserSignInResponse {
//        return userService.modifyUser(authUserInfo.userNo, request)
//    }
//
//    @ApiImplicitParams(
//        ApiImplicitParam(
//            name = "Authorization",
//            value = "로그인 성공 후 access_token",
//            required = true,
//            dataType = "String",
//            dataTypeClass = String::class,
//            paramType = "header"
//        )
//    )
//    @ApiOperation(value = "유저프로필 사진 업데이트", notes = "유저 프로필사진을 업데이트한다.")
//    @PatchMapping("/user/profile")
//    fun userProfileImgModify(
//        @AuthUser authUserInfo: CurrentUserInfo,
//        pictureFile: MultipartFile
//    ) {
//        userService.modifyUserPicture(authUserInfo.userNo, pictureFile)
//    }
//
//    @GetMapping(value = ["/user/profile"], produces = [MediaType.IMAGE_JPEG_VALUE])
//    fun userProfileImgDetails(Authorization: String, picture: String): ResponseEntity<ByteArray> {
//        if (StringUtils.hasText(Authorization) && jwtTokenProvider.validateToken(Authorization)) {
//            return ResponseEntity(fireBaseService.getProfileImg(picture), HttpStatus.OK)
//        }
//
//        return ResponseEntity(HttpStatus.NOT_FOUND)
//    }
}
