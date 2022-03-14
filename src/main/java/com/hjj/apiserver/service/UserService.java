package com.hjj.apiserver.service;

import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.KaKaoProfileDto;
import com.hjj.apiserver.dto.NaverProfileDto;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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


    /* Naver 소셜 로그인 token 요청 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseSignIn getNaverTokenInfo(HashMap<String, String> requestBody) throws Exception {

        Map resultMap = webClient.get()
                .uri(uriBuilder -> uriBuilder.scheme("https")
                        .host(naverTokenHost)
                        .path(naverTokenPath)
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("code", requestBody.get("code"))
                        .queryParam("state", requestBody.get("state"))
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(Map.class)
                .flux().toStream().findFirst().orElseThrow();

        NaverProfileDto naverProfileDto = getNaverProfile((String) resultMap.get("access_token"));
        if(naverProfileDto != null && naverProfileDto.getMessage().equals("success")){
            Optional<UserEntity> user = userRepository.findByUserId(naverProfileDto.getResponse().getId() + "_naver");

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
                UserEntity userEntity = UserDto.builder()
                        .userId(naverProfileDto.getResponse().getId() + "_naver")
                        .userEmail(naverProfileDto.getResponse().getEmail())
                        .nickName(naverProfileDto.getResponse().getNickname())
                        .picture(naverProfileDto.getResponse().getProfile_image())
                        .provider(UserEntity.Provider.NAVER)
                        .build().toEntity();

                userRepository.save(userEntity);
                return this.signInService(userEntity);
            }

        }
        throw new Exception("잘못된 접근입니다.");

    }

    /* Kakao 소셜 로그인 token 요청 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserDto.ResponseSignIn getKakaoTokenInfo(HashMap<String, String> requestBody) throws Exception {

        Map resultMap = webClient.get()
                .uri(uriBuilder -> uriBuilder.scheme("https")
                        .host(kakaoTokenHost)
                        .path(kakaoTokenPath)
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("client_secret", kakaoClientSecret)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("code", requestBody.get("code"))
                        .queryParam("state", requestBody.get("state"))
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new Exception()))
                .bodyToMono(Map.class)
                .flux().toStream().findFirst().orElseThrow();

        KaKaoProfileDto kakaoProfileDto = getKakaoProfile((String) resultMap.get("access_token"));
        if(kakaoProfileDto != null){
            Optional<UserEntity> user = userRepository.findByUserId(kakaoProfileDto.getId() + "_kakao");

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
                UserEntity userEntity = UserDto.builder()
                        .userId(kakaoProfileDto.getId() + "_kakao")
                        .userEmail(kakaoProfileDto.getKakaoAccount().getEmail())
                        .nickName(kakaoProfileDto.getKakaoAccount().getProfile().getNickname())
                        .picture(kakaoProfileDto.getKakaoAccount().getProfile().getProfileImageUrl())
                        .provider(UserEntity.Provider.KAKAO)
                        .build().toEntity();

                userRepository.save(userEntity);
                return this.signInService(userEntity);
            }

        }
        throw new Exception("잘못된 접근입니다.");

    }


    public NaverProfileDto getNaverProfile(String accessToken) {

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
                .flux().toStream().findFirst().orElseThrow();

        return naverProfileDto;
    }

    public KaKaoProfileDto getKakaoProfile(String accessToken) {

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
                .flux().toStream().findFirst().orElseThrow();

        return kakaoProfileDto;
    }

}
