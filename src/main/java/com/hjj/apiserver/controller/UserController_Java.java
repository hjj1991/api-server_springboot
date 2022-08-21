package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError_Java;
import com.hjj.apiserver.common.ApiResponse_Java;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.common.exception.AlreadyExistedUserException;
import com.hjj.apiserver.common.exception.ExistedSocialUserException;
import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepository;
import com.hjj.apiserver.service.FireBaseService;
import com.hjj.apiserver.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;

@Api(tags = {"1. User"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController_Java {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final FireBaseService fireBaseService;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "유저닉네임 중복 조회", notes = "유저닉네임의 중복여부를 확인한다.")
    @GetMapping("/user/{nickName}/exists-nickname")
    public ApiResponse_Java checkNickNameDuplicate(UserEntity user, @PathVariable String nickName) {
        try{
            if(userRepository.existsUserEntityByNickNameAndUserNoNot(nickName, user == null? 0 : user.getUserNo())) {
                return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0003.getMsg(), ApiError_Java.ErrCode.ERR_CODE0003);
            }
            return ApiUtils.success(null);
        }catch (Exception e){
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiOperation(value = "유저 회원가입", notes = "유저 회원가입을 한다.")
    @PostMapping("/user/signup")
    public ApiResponse_Java signUp(@Valid @RequestBody @ApiParam(value = "회원 한 명의 정보를 갖는 객체", required = true) UserDto.RequestSignUpForm form) {
        try{
            if(userRepository.existsUserEntityByUserId(form.getUserId())){
                return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0002.getMsg(), ApiError_Java.ErrCode.ERR_CODE0002);
            }
            if(userRepository.existsUserEntityByNickNameAndUserNoNot(form.getNickName(), 0L)){
                return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0003.getMsg(), ApiError_Java.ErrCode.ERR_CODE0003);
            }

            if( userService.signUp(form) == null){
                return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0003.getMsg(), ApiError_Java.ErrCode.ERR_CODE0003);
            }

            return ApiUtils.success(null);
        }catch (Exception e){
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0004.getMsg(), ApiError_Java.ErrCode.ERR_CODE0004);
        }
    }

    @ApiOperation(value = "유저 로그인", notes ="유저 로그인을 한다.")
    @PostMapping("/user/signin")
    public ApiResponse_Java<UserDto.ResponseSignIn> signIn(@RequestBody UserDto.RequestSignInForm form) throws ExistedSocialUserException, UserNotFoundException {
        UserEntity userEntity = userRepository.findByUserId(form.getUserId()).orElseThrow(UserNotFoundException::new);
        if(userEntity.getProvider() != null)
            throw new ExistedSocialUserException();
        if(!passwordEncoder.matches(form.getUserPw(), userEntity.getUserPw()))
            throw new BadCredentialsException("패스워드가 일치하지 않습니다.");

        return ApiUtils.success(userService.signIn(userEntity));
    }

    @ApiOperation(value = "소셜유저 로그인", notes ="소셜유저 로그인을 한다.")
    @PostMapping("/user/social/signin")
    public ApiResponse_Java socialSignIn(@RequestBody HashMap<String, String> requestBody) {
        try{
            String socialType = requestBody.get("provider");
            if(socialType == null){
                throw new IllegalArgumentException("잘못된 sns타입입니다.");
            }

            return ApiUtils.success(userService.socialSinIn(requestBody));
        }catch (UserNotFoundException e) {
            log.error("[UserController] socialSignIn Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0001.getMsg(), ApiError_Java.ErrCode.ERR_CODE0001);
        }catch (Exception e){
            log.error("[UserController] socialSignIn Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiOperation(value = "소셜유저 회원가입", notes ="소셜유저 회원가입을 한다.")
    @PutMapping("/user/social/signup")
    public ApiResponse_Java socialSignUp(@RequestBody HashMap<String, String> requestBody) {
        try{
            String socialType = requestBody.get("provider");
            if(socialType == null){
                throw new IllegalArgumentException("잘못된 sns타입입니다.");
            }

            userService.socialSinUp(requestBody);

            return ApiUtils.success(null);
        }catch (AlreadyExistedUserException e){
            log.error("[UserController] socialSignIn Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError_Java.ErrCode.ERR_CODE0006);
        }catch (Exception e){
            log.error("[UserController] socialSignIn Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "기존 유저를 소셜계정 연동", notes ="기존 유저를 소셜유게정 연동 한다.")
    @PatchMapping("/user/social/mapping")
    public ApiResponse_Java socialMapping(UserEntity user, @RequestBody HashMap<String, String> requestBody) {
        try{
            String socialType = requestBody.get("provider");
            if(socialType == null){
                throw new IllegalArgumentException("잘못된 sns타입입니다.");
            }

            userService.socialMapping(user, requestBody);

            return ApiUtils.success(null);
        }catch (AlreadyExistedUserException e){
            log.error("[UserController] socialMapping Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError_Java.ErrCode.ERR_CODE0006);
        }catch (Exception e){
            log.error("[UserController] socialMapping Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiOperation(value = "AcessToken 재발급", notes ="AcessToken을 재발급한다.")
    @PostMapping("/user/oauth/token")
    public ApiResponse_Java reIssueeToken(@RequestBody UserDto.RequestReIssueToken form) {
        try{
            return ApiUtils.success(userService.reIssueeToken(form));
        }catch (Exception e){
            log.error("[UserController] reIssueeToken Error form: {}, {}", form, e);
            return ApiUtils.error("token 재발급에 실패했습니다.", ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "유저정보 상세조회", notes ="유저 정보를 상세 조회한다.")
    @GetMapping("/user")
    public ApiResponse_Java userDetails(UserEntity user) {
        try{
            return ApiUtils.success(userService.findUser(user.getUserNo()));
        }catch (Exception e){
            log.error("[UserController] updateUser Error userNo: {}, {}", user.getUserNo(), e);
            return ApiUtils.error("유저 정보 조회가 실패했습니다.", ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "유저정보 업데이트", notes ="유저 정보를 업데이트한다.")
    @PatchMapping("/user")
    public ApiResponse_Java userModify(UserEntity user, @RequestBody @Valid UserDto.RequestUserUpdateForm form) {
        try{
            return ApiUtils.success(userService.modifyUser(user, form));
        }catch (Exception e){
            log.error("[UserController] updateUser Error form: {}, {}", form, e);
            return ApiUtils.error("유저 정보 수정이 실패했습니다.", ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "유저프로필 사진 업데이트", notes ="유저 프로필사진을 업데이트한다.")
    @PatchMapping("/user/profile")
    public ApiResponse_Java userProfileImgModify(UserEntity user, MultipartFile pictureFile) {
        try{

            userService.modifyUserPicture(user, pictureFile);

            return ApiUtils.success(null);
        }catch (Exception e){
            log.error("[UserController] updateProfileImg Error userNo: {}, pictureFile: {}, Exception: {} ", user.getUserNo(), pictureFile, e);
            return ApiUtils.error("이미지 변경이 실패했습니다.", ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @GetMapping(value = "/user/profile", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity userProfileImgDetails(String Authorization, String picture) {
        try{
            if (StringUtils.hasText(Authorization) && jwtTokenProvider.validateToken(Authorization)) {
                return new ResponseEntity(fireBaseService.getProfileImg(picture), HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("[UserController] getProfileImg Error form: {}", e);

        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
