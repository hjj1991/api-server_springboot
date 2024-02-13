package com.hjj.apiserver.adapter.`in`.web.user

import com.hjj.apiserver.adapter.`in`.web.user.request.UserSignInRequest
import com.hjj.apiserver.adapter.`in`.web.user.request.UserSignUpRequest
import com.hjj.apiserver.adapter.`in`.web.user.response.ExistsNickNameResponse
import com.hjj.apiserver.adapter.`in`.web.user.response.ExistsUserIdResponse
import com.hjj.apiserver.adapter.`in`.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.`in`.user.GetUserUseCase
import com.hjj.apiserver.application.port.`in`.user.UserCredentialUseCase
import com.hjj.apiserver.application.port.`in`.user.WriteUserUseCase
import com.hjj.apiserver.application.port.`in`.user.command.CheckUserNickNameDuplicateCommand
import com.hjj.apiserver.application.port.`in`.user.command.RegisterUserCommand
import com.hjj.apiserver.application.port.`in`.user.command.SignInUserCommand
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.adapter.`in`.web.user.request.ReIssueTokenRequest
import com.hjj.apiserver.adapter.`in`.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.util.AuthUser
import com.hjj.apiserver.util.logger
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val writeUserUseCase: WriteUserUseCase,
    @field:Qualifier("generalUserCredentialService")
    private val userCredentialUseCase: UserCredentialUseCase,
) {
    private val log = logger()

    @GetMapping("/users/exists-nickname/{nickName}")
    fun checkUserNickNameDuplicate(
        @AuthUser authUser: User,
        @PathVariable("nickName") nickName: String
    ): ExistsNickNameResponse {
        val command = CheckUserNickNameDuplicateCommand(authUser, nickName)
        return ExistsNickNameResponse(getUserUseCase.existsNickName(command))
    }

    @GetMapping("/users/exists-id/{userId}")
    fun checkUserIdDuplicate(
        @PathVariable("userId") userId: String
    ): ExistsUserIdResponse {
        return ExistsUserIdResponse(userCredentialUseCase.existsUserId(userId))
    }

    @PostMapping("/users/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @Valid @RequestBody request: UserSignUpRequest
    ) {
        val registerUserCommand =
            RegisterUserCommand(request.userId, request.nickName, request.userEmail, request.userPw, Provider.GENERAL)
        writeUserUseCase.signUp(registerUserCommand)
    }

    @PostMapping("/users/signin")
    fun signIn(
        @Valid @RequestBody request: UserSignInRequest
    ): UserSignInResponse {
        val signInUserCommand = SignInUserCommand(request.userId, request.userPw, provider = Provider.GENERAL)
        return writeUserUseCase.signIn(signInUserCommand)
    }

    @PostMapping("/users/oauth/reissue-token")
    fun reIssueToken(@RequestBody request: ReIssueTokenRequest): UserReIssueTokenResponse {
        return userService.reIssueToken(request.refreshToken)

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