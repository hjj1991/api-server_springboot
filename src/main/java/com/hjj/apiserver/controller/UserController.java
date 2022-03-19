package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepository;
import com.hjj.apiserver.service.FireBaseService;
import com.hjj.apiserver.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@Api(tags = {"1. User"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final FireBaseService fireBaseService;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "유저Id 중복 조회", notes = "유저id의 중복여부를 확인한다.")
    @GetMapping("/user/{userId}/exists")
    public ApiResponse checkUserIdDuplicate(@PathVariable String userId) {
        if(userRepository.existsUserEntityByUserId(userId)){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0002.getMsg(), ApiError.ErrCode.ERR_CODE0002);
        }else{
            return ApiUtils.success(null);
        }
    }

    @ApiOperation(value = "유저 회원가입", notes = "유저 회원가입을 한다.")
    @PostMapping("/user/signup")
    public ApiResponse signUp(@Valid @RequestBody UserDto.RequestSignUpForm form) {
        try{
            if(userRepository.existsUserEntityByUserId(form.getUserId())){
                return ApiUtils.error(ApiError.ErrCode.ERR_CODE0002.getMsg(), ApiError.ErrCode.ERR_CODE0002);
            }
            if(userRepository.existsUserEntityByNickName(form.getNickName())){
                return ApiUtils.error(ApiError.ErrCode.ERR_CODE0003.getMsg(), ApiError.ErrCode.ERR_CODE0003);
            }

            if( userService.signUpService(form) == null){
                return ApiUtils.error(ApiError.ErrCode.ERR_CODE0003.getMsg(), ApiError.ErrCode.ERR_CODE0003);
            }

            return ApiUtils.success(null);
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0004.getMsg(), ApiError.ErrCode.ERR_CODE0004);
        }



    }

    @ApiOperation(value = "유저 로그인", notes ="유저 로그인을 한다.")
    @PostMapping("/user/signin")
    public ApiResponse<UserDto.ResponseSignIn> signIn(@RequestBody UserDto.RequestSignInForm form) {
        UserEntity userEntity = userRepository.findByUserId(form.getUserId()).orElseThrow(() -> new UsernameNotFoundException(""));
        if(!passwordEncoder.matches(form.getUserPw(), userEntity.getUserPw()))
            throw new UsernameNotFoundException("");

        return ApiUtils.success(userService.signInService(userEntity));
    }

    @ApiOperation(value = "소셜유저 로그인", notes ="소셜유저 로그인을 한다.")
    @PostMapping("/user/social/signin")
    public ApiResponse socialSignIn(@RequestBody HashMap<String, String> requestBody) {
        try{
            String socialType = requestBody.get("provider");
            if(socialType == null){
                throw new IllegalArgumentException("잘못된 sns타입입니다.");
            }

            UserDto.ResponseSignIn responseSignIn = userService.socialSinUpService(requestBody);

            if(responseSignIn == null){
                return ApiUtils.error(null, ApiError.ErrCode.ERR_CODE9999);
            }
            return ApiUtils.success(responseSignIn);
        }catch (Exception e){
            log.error("[UserController] socialSignIn Error requestBody: {}, {}", requestBody, e);
            return ApiUtils.error(e.getMessage(), ApiError.ErrCode.ERR_CODE9999);
        }

    }

    @ApiOperation(value = "AcessToken 재발급", notes ="AcessToken을 재발급한다.")
    @PostMapping("/user/oauth/token")
    public ApiResponse reIssueeToken(@RequestBody UserDto.RequestReIssueToken form) {
        try{
            return ApiUtils.success(userService.reIssueeToken(form));
        }catch (Exception e){
            log.error("[UserController] reIssueeToken Error form: {}, {}", form, e);
            return ApiUtils.error("token 재발급에 실패했습니다.", ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiOperation(value = "유저정보 업데이트", notes ="유저 정보를 업데이트한다.")
    @PatchMapping("/user")
    public ApiResponse updateUser(@AuthenticationPrincipal TokenDto user, UserDto.RequestUserUpdateForm form) {
        try{


            form.setUserNo(user.getUserNo());
            userService.updateUser(form);

            return ApiUtils.success(null);
        }catch (Exception e){
            log.error("[UserController] updateUser Error form: {}, {}", form, e);
            return ApiUtils.error("token 재발급에 실패했습니다.", ApiError.ErrCode.ERR_CODE9999);
        }
    }


    @GetMapping(value = "/user/profile", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity getProfileImg(String access_token, String picture) {
        try{
            if (StringUtils.hasText(access_token) && jwtTokenProvider.validateToken(access_token)) {
                return new ResponseEntity(fireBaseService.getProfileImg(picture), HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("[UserController] getProfileImg Error form: {}", e);

        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);

    }
}
