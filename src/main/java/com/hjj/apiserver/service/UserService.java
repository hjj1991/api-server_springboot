package com.hjj.apiserver.service;

import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService  {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;


    /* 로그인 로직 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseSignIn signInService(UserEntity userEntity){

        HashMap<JwtTokenProvider.TokenKey, Object> token = jwtTokenProvider.createToken(userEntity);
        String refreshToken = jwtTokenProvider.createRefreshToken(userEntity);

        /* 로그인 시간, 리프레쉬 토큰 업데이트 */
        userEntity.updateUserLogin(refreshToken);

        UserDto.ResponseSignIn responseSignIn = modelMapper.map(userEntity, UserDto.ResponseSignIn.class);
        responseSignIn.setAccessToken((String) token.get(JwtTokenProvider.TokenKey.TOKEN));
        responseSignIn.setExpireTime((Long) token.get(JwtTokenProvider.TokenKey.EXPIRETIME));
        responseSignIn.setRefreshToken(refreshToken);

        return responseSignIn;

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseReIssueToken reIssueeToken(UserDto.RequestReIssueToken form) throws Exception {
        String refreshToken = form.getRefreshToken();
        if(!jwtTokenProvider.validateToken(refreshToken))
            throw new Exception("유효하지 않은 토큰입니다.");

        UserEntity user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new Exception("유효한 토큰이 존재하지 않습니다."));

        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);
        HashMap<JwtTokenProvider.TokenKey, Object> newAccessToken = jwtTokenProvider.createToken(user);

        /* refresh token 업데이트 */
        user.updateUserLogin(newRefreshToken);

        UserDto.ResponseReIssueToken responseReIssueToken = new UserDto.ResponseReIssueToken();
        responseReIssueToken.setAccessToken((String) newAccessToken.get(JwtTokenProvider.TokenKey.TOKEN));
        responseReIssueToken.setExpireTime((Long) newAccessToken.get(JwtTokenProvider.TokenKey.EXPIRETIME));
        responseReIssueToken.setRefreshToken(newRefreshToken);

        return responseReIssueToken;





    }

}
