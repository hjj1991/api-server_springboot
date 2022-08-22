package com.hjj.apiserver.service.user

import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.common.TokenType
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.*
import com.hjj.apiserver.dto.user.request.UserAddRequest
import com.hjj.apiserver.dto.user.request.UserModifyRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.response.KakaoProfileResponse
import com.hjj.apiserver.dto.user.response.NaverProfileResponse
import com.hjj.apiserver.dto.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.dto.user.response.UserSignInResponse
import com.hjj.apiserver.repository.user.UserLogRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.service.FireBaseService
import com.hjj.apiserver.util.logger
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.time.LocalDateTime
import javax.imageio.ImageIO

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userLogService: UserLogService,
    private val passwordEncoder: PasswordEncoder,
    private val userLogRepository: UserLogRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val webClient: WebClient,
    private val fireBaseService: FireBaseService,

    @Value(value = "\${social.naver.url.token.host}")
    private val naverTokenHost: String,
    @Value(value = "\${social.naver.url.token.path}")
    private val  naverTokenPath: String,
    @Value(value = "\${social.naver.url.profile.host}")
    private val  naverProfileHost: String,
    @Value(value = "\${social.naver.url.profile.path}")
    private val  naverProfilePath: String,
    @Value(value = "\${social.naver.client-id}")
    private val  naverClientId: String,
    @Value(value = "\${social.naver.client-secret}")
    private val  naverClientSecret: String,
    @Value(value = "\${social.kakao.url.profile.host}")
    private val  kakaoProfileHost: String,
    @Value(value = "\${social.kakao.url.profile.path}")
    private val  kakaoProfilePath: String,
    @Value(value = "\${social.kakao.url.token.host}")
    private val  kakaoTokenHost: String,
    @Value(value = "\${social.kakao.url.token.path}")
    private val  kakaoTokenPath: String,
    @Value(value = "\${social.kakao.client-id}")
    private val  kakaoClientId: String,
    @Value(value = "\${social.kakao.client-secret}")
    private val  kakaoClientSecret: String,
    @Value(value = "\${app.firebase-storage-uri}")
    private val  firebaseStorageUri: String,
    @Value(value = "\${app.firebase-bucket}")
    private val  firebaseBucket: String,
) {
    companion object{
        const val PROFILE_IMG_PATH = "profile/"
    }
    private val log = logger()



    fun existsUserId(userId: String):Boolean{
        return userRepository.existsUserByUserId(userId)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun signUp(request: UserAddRequest):User {
        val newUser = User(
            userId = request.userId,
            nickName = request.nickName,
            userEmail = request.userEmail,
            userPw = passwordEncoder.encode(request.userPw),
            picture = request.picture,
            provider = request.provider
        )

        val savedUser = userRepository.save(newUser)
        userLogRepository.save(UserLog(logType = LogType.INSERT, user = savedUser))

        return savedUser
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun signIn(request: UserSignInRequest): UserSignInResponse {
        val user = userRepository.findByUserId(request.userId) ?: throw UserNotFoundException()

        /* SNS 로그인 계정인 경우 Exception처리 */
        if(user.isSocialUser()){
            throw ExistedSocialUserException()
        }

        if(!passwordEncoder.matches(request.userPw, user.userPw)){
            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
        }

        val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
        val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        /* 리프레쉬 토큰 업데이트 */
        user.updateUserLogin(refreshToken)

        /* 유저 로그 INSERT */
        userLogService.insertUserLog(UserLog(LocalDateTime.now(), SignInType.GENERAL, LogType.SIGNIN, user))

        return UserSignInResponse(
            user.userId, user.nickName, user.userEmail, user.picture, user.provider, accessToken, refreshToken, user.createdDate, LocalDateTime.now())

    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun reIssueToken(refreshToken: String): UserReIssueTokenResponse{
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw IllegalAccessException()
        }

        val user = userRepository.findByRefreshToken(refreshToken) ?: throw UserNotFoundException()

        val newRefreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        user.updateUserLogin(newRefreshToken)

        return UserReIssueTokenResponse(jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN), newRefreshToken)
    }


    /* Naver, Naver 소셜 로그인 token 요청 */
    fun findSocialTokenInfo(code:String, state:String, provider: Provider): Map<String, Any> {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.scheme("https")
                    .host(if(provider == Provider.NAVER){
                        naverTokenHost
                    }else{
                        kakaoTokenHost
                    })
                    .path(if(provider == Provider.NAVER){
                        naverTokenPath
                    }else{
                        kakaoTokenPath
                    })
                    .queryParam("client_id",if(provider == Provider.NAVER){
                        naverClientId
                    }else{
                        kakaoClientId
                    })
                    .queryParam("client_secret",if(provider == Provider.NAVER){
                        naverClientSecret
                    }else{
                        kakaoClientSecret
                    })
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("code", code)
                    .queryParam("state", state)
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Exception("접속 실패하였습니다.")) }
            .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .flux().toStream().findFirst().get()

    }

    fun findNaverProfile(accessToken: String): NaverProfileResponse{
        return webClient.get()
            .uri{uriBuilder:UriBuilder -> uriBuilder
                .scheme("https")
                .host(naverProfileHost)
                .path(naverProfilePath)
                .build()}
            .header("Authorization", "Bearer ${accessToken}")
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Exception("접속 실패하였습니다.")) }
            .bodyToMono(NaverProfileResponse::class.java)
            .flux().toStream().findFirst().get()
    }

    fun findKakaoProfile(accessToken: String): KakaoProfileResponse{
        return webClient.get()
            .uri{uriBuilder:UriBuilder -> uriBuilder
                .scheme("https")
                .host(kakaoProfileHost)
                .path(kakaoProfilePath)
                .build()}
            .header("Authorization", "Bearer ${accessToken}")
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Exception("접속 실패하였습니다.")) }
            .bodyToMono(KakaoProfileResponse::class.java)
            .flux().toStream().findFirst().get()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyUser(user: User, request: UserModifyRequest): UserSignInResponse {
        if(!passwordEncoder.matches(request.userPw, user.userPw)){
            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
        }

        user.updateUser(request.nickName, request.userEmail)
        userRepository.flush()
        userLogService.insertUserLog(UserLog(logType = LogType.MODIFY, user = user))


        val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
        val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        /* 리프레쉬 토큰 업데이트 */
        user.updateUserLogin(refreshToken)

        return UserSignInResponse(
            user.userId, user.nickName, user.userEmail, user.picture, user.provider, accessToken, refreshToken, user.createdDate)
    }


    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyUserPicture(user: User, pictureFile: MultipartFile) {
        /*  이미지 썸네일 제작 프론트에서 처리하도록 수정
        val bufferedImage = ImageIO.read(pictureFile.inputStream)
        val imgWidth = bufferedImage.height.coerceAtMost(bufferedImage.width)
        val scaledImg = Scalr.crop(
            bufferedImage,
            (bufferedImage.width - imgWidth) / 2,
            (bufferedImage.height - imgWidth) / 2,
            imgWidth,
            imgWidth,
            null
        )
        val resizedImg = Scalr.resize(scaledImg, 100, 100, null)
        */
        /* outputStream에 image객체 저장 *//*

        ImageIO.write(resizedImg, "jpg", ByteArrayOutputStream())
        */

        val fileName = PROFILE_IMG_PATH + user.userNo + ".png"
        fireBaseService.putProfileImg(pictureFile.bytes, fileName)
        val picturePath = firebaseStorageUri + firebaseBucket + "/o/" + URLEncoder.encode(fileName, "UTF-8") + "?alt=media"
        user.updateUser(picture = picturePath)
    }

}