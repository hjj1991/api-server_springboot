package com.hjj.apiserver.service;

import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.domain.UserLogEntity;
import com.hjj.apiserver.dto.KaKaoProfileDto;
import com.hjj.apiserver.dto.NaverProfileDto;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.dto.UserLogDto;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService  {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;
    private final PasswordEncoder passwordEncoder;
    private final UserLogService userLogService;

    @Value(value = "${social.naver.url.token.host}")
    private String naverTokenHost;
    @Value(value = "${social.naver.url.token.path}")
    private String naverTokenPath;
    @Value(value = "${social.naver.url.profile.host}")
    private String naverProfileHost;
    @Value(value = "${social.naver.url.profile.path}")
    private String naverProfilePath;
    @Value(value = "${social.naver.client-id}")
    private String naverClientId;
    @Value(value = "${social.naver.client-secret}")
    private String naverClientSecret;

    @Value(value = "${social.kakao.url.profile.host}")
    private String kakaoProfileHost;
    @Value(value = "${social.kakao.url.profile.path}")
    private String kakaoProfilePath;
    @Value(value = "${social.kakao.url.token.host}")
    private String kakaoTokenHost;
    @Value(value = "${social.kakao.url.token.path}")
    private String kakaoTokenPath;
    @Value(value = "${social.kakao.client-id}")
    private String kakaoClientId;
    @Value(value = "${social.kakao.client-secret}")
    private String kakaoClientSecret;



    /* 로그인 로직 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseSignIn signInService(UserEntity userEntity){

        HashMap<JwtTokenProvider.TokenKey, Object> token = jwtTokenProvider.createToken(userEntity);
        String refreshToken = jwtTokenProvider.createRefreshToken(userEntity);

        /* 리프레쉬 토큰 업데이트 */
        userEntity.updateUserLogin(refreshToken);
        UserLogDto userLogDto = new UserLogDto();
        userLogDto.setSignInType(UserLogEntity.SignInType.GENERAL);
        userLogDto.setUserInfo(userEntity);
        userLogDto.setLogType(UserLogEntity.LogType.SIGNIN);
        userLogDto.setLoginDateTime(LocalDateTime.now());
        userLogService.insertUserLog(userLogDto);

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


    /* Naver 소셜 로그인 token 요청 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map getNaverTokenInfo(String code, String state) throws Exception {

        Map resultMap = webClient.get()
                .uri(uriBuilder -> uriBuilder.scheme("https")
                        .host(naverTokenHost)
                        .path(naverTokenPath)
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(Map.class)
                .flux().toStream().findFirst().orElseThrow(Exception::new);

        return resultMap;


    }

    /* Kakao 소셜 로그인 token 요청 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map getKakaoTokenInfo(String code, String state) throws Exception {

        Map resultMap = webClient.get()
                .uri(uriBuilder -> uriBuilder.scheme("https")
                        .host(kakaoTokenHost)
                        .path(kakaoTokenPath)
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("client_secret", kakaoClientSecret)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(Map.class)
                .flux().toStream().findFirst().orElseThrow(Exception::new);

        return resultMap;

    }


    public NaverProfileDto getNaverProfile(String accessToken) throws Exception {

        NaverProfileDto naverProfileDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(naverProfileHost)
                        .path(naverProfilePath)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(NaverProfileDto.class)
                .flux().toStream().findFirst().orElseThrow(Exception::new);

        return naverProfileDto;
    }

    public KaKaoProfileDto getKakaoProfile(String accessToken) throws Exception {

        KaKaoProfileDto kakaoProfileDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(kakaoProfileHost)
                        .path(kakaoProfilePath)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(KaKaoProfileDto.class)
                .flux().toStream().findFirst().orElseThrow(Exception::new);

        return kakaoProfileDto;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserEntity signUpService(UserDto.RequestSignUpForm form) throws Exception {

        UserDto userDto = modelMapper.map(form, UserDto.class);
        UserEntity userEntity = userRepository.save(userDto.toEntityWithPasswordEncode(passwordEncoder));
        UserLogDto userLogDto = new UserLogDto();
        userLogDto.setUserInfo(userEntity);
        userLogDto.setLogType(UserLogEntity.LogType.INSERT);
        userLogDto.setCreatedDate(LocalDateTime.now());
        userLogService.insertUserLog(userLogDto);
        return userEntity;

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseSignIn socialSinUpService(HashMap<String, String> requestBody) throws Exception {
        String provider = requestBody.get("provider");

        /* 네이버 가입 로직 */
        if(provider.equals("NAVER")){
            Map resultMap = this.getNaverTokenInfo(requestBody.get("code"), requestBody.get("state"));
            NaverProfileDto naverProfileDto = getNaverProfile((String) resultMap.get("access_token"));
            if(naverProfileDto != null && naverProfileDto.getMessage().equals("success")){
                Optional<UserEntity> user = userRepository.findByProviderAndProviderId(UserEntity.Provider.valueOf(provider), naverProfileDto.getResponse().getId());

                /* 해당 사용자가 이미 가입한 계정인 경우 */
                if(user.isPresent()){
                    return this.signInService(user.get());
                }else{
                    /* 중복된 닉네임이 있을 경우 10자리 랜덤문자열을 생성하여 넣는다. */
                    if(userRepository.existsUserEntityByNickName(naverProfileDto.getResponse().getNickname()) == true){
                        Random random = new Random();
                        naverProfileDto.getResponse().setNickname(random.ints(97, 123)
                                .limit(10)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString());
                    }
                    UserDto userDto = new UserDto();
                    userDto.setProvider(UserEntity.Provider.valueOf(provider));
                    userDto.setProviderId(naverProfileDto.getResponse().getId());
                    userDto.setUserEmail(naverProfileDto.getResponse().getEmail());
                    userDto.setNickName(naverProfileDto.getResponse().getNickname());
                    userDto.setPicture(naverProfileDto.getResponse().getProfile_image());
                    UserEntity userEntity = userDto.toEntity();
                    userRepository.save(userEntity);

                    UserLogDto userLogDto = new UserLogDto();
                    userLogDto.setUserInfo(userEntity);
                    userLogDto.setLogType(UserLogEntity.LogType.INSERT);
                    userLogDto.setCreatedDate(LocalDateTime.now());
                    userLogService.insertUserLog(userLogDto);

                    return this.signInService(userEntity);
                }
            }
        /* 카카오 가입 로직 */
        }else if(provider.equals("KAKAO")){
            Map resultMap = this.getKakaoTokenInfo(requestBody.get("code"), requestBody.get("state"));

            KaKaoProfileDto kakaoProfileDto = getKakaoProfile((String) resultMap.get("access_token"));
            if(kakaoProfileDto != null){
                Optional<UserEntity> user = userRepository.findByProviderAndProviderId(UserEntity.Provider.valueOf(provider), kakaoProfileDto.getId());

                /* 해당 사용자가 이미 가입한 계정인 경우 */
                if(user.isPresent()){
                    return this.signInService(user.get());
                }else{
                    /* 중복된 닉네임이 있을 경우 10자리 랜덤문자열을 생성하여 넣는다. */
                    if(userRepository.existsUserEntityByNickName(kakaoProfileDto.getKakaoAccount().getProfile().getNickname()) == true){
                        Random random = new Random();
                        kakaoProfileDto.getKakaoAccount().getProfile().setNickname(random.ints(97, 123)
                                .limit(10)
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString());
                    }
                    UserDto userDto = new UserDto();
                    userDto.setProvider(UserEntity.Provider.valueOf(provider));
                    userDto.setProviderId(kakaoProfileDto.getId());
                    userDto.setUserEmail(kakaoProfileDto.getKakaoAccount().getEmail());
                    userDto.setNickName(kakaoProfileDto.getKakaoAccount().getProfile().getNickname());
                    userDto.setPicture(kakaoProfileDto.getKakaoAccount().getProfile().getProfileImageUrl());
                    UserEntity userEntity = userDto.toEntity();

                    userRepository.save(userEntity);

                    UserLogDto userLogDto = new UserLogDto();
                    userLogDto.setUserInfo(userEntity);
                    userLogDto.setLogType(UserLogEntity.LogType.INSERT);
                    userLogDto.setCreatedDate(LocalDateTime.now());
                    userLogService.insertUserLog(userLogDto);
                    return this.signInService(userEntity);
                }

            }
        }

        return null;
    }
}
