package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.AlreadyExistedUserException;
import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.domain.UserLogEntity;
import com.hjj.apiserver.dto.*;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final WebClient webClient;
    private final PasswordEncoder passwordEncoder;
    private final UserLogService userLogService;
    private final FireBaseService fireBaseService;
    private final static String PROFILE_IMG_PATH = "profile/";

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

    @Value(value = "${app.firebase-storage-uri}")
    private String firebaseStorageUri;
    @Value(value = "${app.firebase-bucket}")
    private String firebaseBucket;



    /* Naver 소셜 로그인 token 요청 */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map findNaverTokenInfo(String code, String state) throws Exception {

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
    public Map findKakaoTokenInfo(String code, String state) throws Exception {

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


    public NaverProfileDto findNaverProfile(String accessToken) throws Exception {

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

    public KaKaoProfileDto findKakaoProfile(String accessToken) throws Exception {

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
    public UserEntity signUp(UserDto.RequestSignUpForm form) throws Exception {

        UserDto userDto = modelMapper.map(form, UserDto.class);
        UserEntity userEntity = userRepository.save(userDto.toEntityWithPasswordEncode(passwordEncoder));
        UserLogDto userLogDto = new UserLogDto();
        userLogDto.setUserEntity(userEntity);
        userLogDto.setLogType(UserLogEntity.LogType.INSERT);
        userLogDto.setCreatedDate(LocalDateTime.now());
        userLogService.insertUserLog(userLogDto);
        return userEntity;

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void modifyUserPicture(UserEntity user, MultipartFile pictureFile) throws Exception {
        if(pictureFile != null){
            /* 이미지 썸네일 제작 프론트에서 처리하도록 수정 */
//            BufferedImage bufferedImage = ImageIO.read(pictureFile.getInputStream());
//
//            int imgwidth = Math.min(bufferedImage.getHeight(),  bufferedImage.getWidth());
//            int imgheight = imgwidth;
//
//            BufferedImage scaledImage = Scalr.crop(bufferedImage, (bufferedImage.getWidth() - imgwidth)/2, (bufferedImage.getHeight() - imgheight)/2, imgwidth, imgheight, null);
//            BufferedImage resizedImage = Scalr.resize(scaledImage, 100, 100, null);
//
//            // outputstream에 image객체를 저장
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "jpg", os);
//            byte[] bytes = os.toByteArray();




            String fileName = PROFILE_IMG_PATH + user.getUserNo() + ".png";
            fireBaseService.putProfileImg(pictureFile.getBytes(), fileName);
            UserDto userDto = new UserDto();
//            userDto.setPicture(fileName);
            userDto.setPicture(firebaseStorageUri + firebaseBucket + "/o/" + URLEncoder.encode(fileName, "UTF-8") + "?alt=media");
            user.updateUser(userDto);
        }

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void socialSinUp(HashMap<String, String> requestBody) throws Exception {
        String provider = requestBody.get("provider");

        /* 네이버 가입 로직 */
        if(provider.equals("NAVER")){
            Map resultMap = this.findNaverTokenInfo(requestBody.get("code"), requestBody.get("state"));
            NaverProfileDto naverProfileDto = findNaverProfile((String) resultMap.get("access_token"));
            if(naverProfileDto != null && naverProfileDto.getMessage().equals("success")){
                Optional<UserEntity> user = userRepository.findByProviderAndProviderId(UserEntity.Provider.valueOf(provider), naverProfileDto.getResponse().getId());

                /* 해당 사용자가 이미 가입한 계정인 경우 */
                if(user.isPresent()){
                    throw new AlreadyExistedUserException();
                }else{
                    /* 중복된 닉네임이 있을 경우 10자리 랜덤문자열을 생성하여 넣는다. */
                    if(userRepository.existsUserEntityByNickNameAndUserNoNot(naverProfileDto.getResponse().getNickname(), 0L) == true){
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
                    userLogDto.setUserEntity(userEntity);
                    userLogDto.setLogType(UserLogEntity.LogType.INSERT);
                    userLogDto.setCreatedDate(LocalDateTime.now());
                    userLogService.insertUserLog(userLogDto);

                }
            }
        /* 카카오 가입 로직 */
        }else if(provider.equals("KAKAO")){
            Map resultMap = this.findKakaoTokenInfo(requestBody.get("code"), requestBody.get("state"));

            KaKaoProfileDto kakaoProfileDto = findKakaoProfile((String) resultMap.get("access_token"));
            if(kakaoProfileDto != null){
                Optional<UserEntity> user = userRepository.findByProviderAndProviderId(UserEntity.Provider.valueOf(provider), kakaoProfileDto.getId());

                /* 해당 사용자가 이미 가입한 계정인 경우 */
                if(user.isPresent()){
                    throw new AlreadyExistedUserException();
                }else{
                    /* 중복된 닉네임이 있을 경우 10자리 랜덤문자열을 생성하여 넣는다. */
                    if(userRepository.existsUserEntityByNickNameAndUserNoNot(kakaoProfileDto.getKakaoAccount().getProfile().getNickname(), 0L) == true){
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
                    userLogDto.setUserEntity(userEntity);
                    userLogDto.setLogType(UserLogEntity.LogType.INSERT);
                    userLogDto.setCreatedDate(LocalDateTime.now());
                    userLogService.insertUserLog(userLogDto);
                }

            }
        }

    }


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void socialMapping(UserEntity user, HashMap<String, String> requestBody) throws Exception {
        String provider = requestBody.get("provider");

        /* 네이버 연동로직 */
        if(provider.equals("NAVER")) {
            Map resultMap = this.findNaverTokenInfo(requestBody.get("code"), requestBody.get("state"));
            NaverProfileDto naverProfileDto = findNaverProfile((String) resultMap.get("access_token"));
            if(naverProfileDto != null && naverProfileDto.getMessage().equals("success")
                    && !userRepository.existsUserEntityByProviderIdAndProviderAndDeleteYn(naverProfileDto.getResponse().getId(), UserEntity.Provider.NAVER, 'N')){
                if(user.getProvider() != null){
                    throw new AlreadyExistedUserException();
                }
                UserDto userDto = new UserDto();
                userDto.setProvider(UserEntity.Provider.NAVER);
                userDto.setProviderId(naverProfileDto.getResponse().getId());
                userDto.setProviderConnectDate(LocalDateTime.now());
                user.updateUser(userDto);
                userRepository.flush();

                UserLogDto userLogDto = new UserLogDto();
                userLogDto.setLogType(UserLogEntity.LogType.MODIFY);
                userLogDto.setUserEntity(user);
                userLogService.insertUserLog(userLogDto);
            }
        }else if(provider.equals("KAKAO")){
            Map resultMap = this.findKakaoTokenInfo(requestBody.get("code"), requestBody.get("state"));

            KaKaoProfileDto kakaoProfileDto = findKakaoProfile((String) resultMap.get("access_token"));
            if(kakaoProfileDto != null && !userRepository.existsUserEntityByProviderIdAndProviderAndDeleteYn(kakaoProfileDto.getId(), UserEntity.Provider.KAKAO, 'N')){
                if (user.getProvider() != null) {
                    throw new AlreadyExistedUserException();
                }
                UserDto userDto = new UserDto();
                userDto.setProvider(UserEntity.Provider.KAKAO);
                userDto.setProviderId(kakaoProfileDto.getId());
                userDto.setProviderConnectDate(LocalDateTime.now());
                user.updateUser(userDto);

                UserLogDto userLogDto = new UserLogDto();
                userLogDto.setLogType(UserLogEntity.LogType.MODIFY);
                userLogDto.setUserEntity(user);
                userLogService.insertUserLog(userLogDto);
            }
        }
    }

    public UserDto.ResponseUserDetails findUser(Long userNo) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findUserLeftJoinUserLogByUserNo(userNo);
        if(userEntity == null){
            throw new UserNotFoundException();
        }
        UserDto.ResponseUserDetails responseUserDetails = modelMapper.map(userEntity, UserDto.ResponseUserDetails.class);
        if(userEntity.getUserLogEntityList().get(0) != null) {
            responseUserDetails.setLastLoginDateTime(userEntity.getUserLogEntityList().get(0).getLoginDateTime());
        }
        return responseUserDetails;
    }
}
