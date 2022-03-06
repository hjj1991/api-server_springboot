package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"1. User"})
@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
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
        if(userRepository.existsUserEntityByUserId(form.getUserId())){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0002.getMsg(), ApiError.ErrCode.ERR_CODE0002);
        }
        if(userRepository.existsUserEntityByName(form.getNickName())){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0003.getMsg(), ApiError.ErrCode.ERR_CODE0003);
        }

        UserDto userDto = modelMapper.map(form, UserDto.class);
        UserEntity userEntity = userRepository.save(userDto.toEntityWithPasswordEncode(passwordEncoder));
        if(userEntity != null){
            return ApiUtils.success(null);
        }

        return ApiUtils.error(ApiError.ErrCode.ERR_CODE0004.getMsg(), ApiError.ErrCode.ERR_CODE0004);
    }

    @ApiOperation(value = "유저 로그인", notes ="유저 로그인을 한다.")
    @PostMapping("/user/signin")
    public ApiResponse<UserDto.ResponseSignIn> signIn(@RequestBody UserDto.RequestSignInForm form) {
        UserEntity userEntity = userRepository.findByUserId(form.getUserId()).orElseThrow(() -> new UsernameNotFoundException(""));
        if(!passwordEncoder.matches(form.getUserPw(), userEntity.getUserPw()))
            throw new UsernameNotFoundException("");

        UserDto.ResponseSignIn responseSignIn = modelMapper.map(userEntity, UserDto.ResponseSignIn.class);

        List<String> jwtToken = jwtTokenProvider.createToken(userEntity);

        responseSignIn.setAccessToken(jwtToken.get(0));
        responseSignIn.setExpireTime(jwtToken.get(1));

        return ApiUtils.success(responseSignIn);


    }
}
