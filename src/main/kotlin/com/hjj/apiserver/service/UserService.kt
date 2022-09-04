package com.hjj.apiserver.service

import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.common.TokenType
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.*
import com.hjj.apiserver.dto.user.request.UserModifyRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.request.UserSinUpRequest
import com.hjj.apiserver.dto.user.response.*
import com.hjj.apiserver.repository.user.UserLogRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.*

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
    private val naverTokenPath: String,
    @Value(value = "\${social.naver.url.profile.host}")
    private val naverProfileHost: String,
    @Value(value = "\${social.naver.url.profile.path}")
    private val naverProfilePath: String,
    @Value(value = "\${social.naver.client-id}")
    private val naverClientId: String,
    @Value(value = "\${social.naver.client-secret}")
    private val naverClientSecret: String,
    @Value(value = "\${social.kakao.url.profile.host}")
    private val kakaoProfileHost: String,
    @Value(value = "\${social.kakao.url.profile.path}")
    private val kakaoProfilePath: String,
    @Value(value = "\${social.kakao.url.token.host}")
    private val kakaoTokenHost: String,
    @Value(value = "\${social.kakao.url.token.path}")
    private val kakaoTokenPath: String,
    @Value(value = "\${social.kakao.client-id}")
    private val kakaoClientId: String,
    @Value(value = "\${social.kakao.client-secret}")
    private val kakaoClientSecret: String,
    @Value(value = "\${app.firebase-storage-uri}")
    private val firebaseStorageUri: String,
    @Value(value = "\${app.firebase-bucket}")
    private val firebaseBucket: String,
) {
    companion object {
        const val PROFILE_IMG_PATH = "profile/"
    }

    private val log = logger()


    fun existsNickName(nickName: String): Boolean {
        return userRepository.findExistsUserNickName(nickName)
    }

    fun existsUserId(userId: String): Boolean {
        return userRepository.findExistsUserId(userId)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun signUp(request: UserSinUpRequest): User {
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
        if (user.isSocialUser()) {
            throw ExistedSocialUserException()
        }

        if (!passwordEncoder.matches(request.userPw, user.userPw)) {
            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
        }

        val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
        val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        /* 리프레쉬 토큰 업데이트 */
        user.updateUserLogin(refreshToken)

        /* 유저 로그 INSERT */
        userLogService.addUserLog(UserLog(LocalDateTime.now(), SignInType.GENERAL, LogType.SIGNIN, user))

        return UserSignInResponse(
            user.userId,
            user.nickName,
            user.userEmail,
            user.picture,
            user.provider,
            accessToken,
            refreshToken,
            user.createdDate,
            LocalDateTime.now()
        )

    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun reIssueToken(refreshToken: String): UserReIssueTokenResponse {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw IllegalAccessException()
        }

        val user = userRepository.findByRefreshToken(refreshToken) ?: throw UserNotFoundException()

        val newRefreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        user.updateUserLogin(newRefreshToken)

        return UserReIssueTokenResponse(jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN), newRefreshToken)
    }


    /* Naver, Naver 소셜 로그인 token 요청 */
    fun findSocialTokenInfo(code: String, state: String, provider: Provider): Map<String, Any> {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.scheme("https")
                    .host(
                        if (provider == Provider.NAVER) {
                            naverTokenHost
                        } else {
                            kakaoTokenHost
                        }
                    )
                    .path(
                        if (provider == Provider.NAVER) {
                            naverTokenPath
                        } else {
                            kakaoTokenPath
                        }
                    )
                    .queryParam(
                        "client_id", if (provider == Provider.NAVER) {
                            naverClientId
                        } else {
                            kakaoClientId
                        }
                    )
                    .queryParam(
                        "client_secret", if (provider == Provider.NAVER) {
                            naverClientSecret
                        } else {
                            kakaoClientSecret
                        }
                    )
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

    fun findNaverProfile(accessToken: String): NaverProfileResponse {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host(naverProfileHost)
                    .path(naverProfilePath)
                    .build()
            }
            .header("Authorization", "Bearer ${accessToken}")
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Exception("접속 실패하였습니다.")) }
            .bodyToMono(NaverProfileResponse::class.java)
            .flux().toStream().findFirst().get()
    }

    fun findKakaoProfile(accessToken: String): KakaoProfileResponse {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host(kakaoProfileHost)
                    .path(kakaoProfilePath)
                    .build()
            }
            .header("Authorization", "Bearer ${accessToken}")
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Exception("접속 실패하였습니다.")) }
            .bodyToMono(KakaoProfileResponse::class.java)
            .flux().toStream().findFirst().get()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyUser(userNo: Long, request: UserModifyRequest): UserSignInResponse {

        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(request.userPw, user.userPw)) {
            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
        }

        user.updateUser(request.nickName, request.userEmail)
        userRepository.flush()
        userLogService.addUserLog(UserLog(logType = LogType.MODIFY, user = user))


        val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
        val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
        /* 리프레쉬 토큰 업데이트 */
        user.updateUserLogin(refreshToken)

        return UserSignInResponse(
            user.userId,
            user.nickName,
            user.userEmail,
            user.picture,
            user.provider,
            accessToken,
            refreshToken,
            user.createdDate
        )
    }


    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyUserPicture(userNo: Long, pictureFile: MultipartFile) {
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

        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()

        val fileName = PROFILE_IMG_PATH + user.userNo + ".png"
        fireBaseService.putProfileImg(pictureFile.bytes, fileName)
        val picturePath =
            firebaseStorageUri + firebaseBucket + "/o/" + URLEncoder.encode(fileName, "UTF-8") + "?alt=media"
        user.updateUser(picture = picturePath)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun socialSignUp(request: HashMap<String, String>) {
        val provider = request["provider"]

        if (request["code"] == null || request["state"] == null) {
            throw IllegalStateException()
        }

        /* 네이버 가입 로직 */
        if (provider == Provider.NAVER.name) {
            val resultMap = findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.NAVER)
            val naverProfile =
                resultMap["access_token"]?.let { findNaverProfile(it as String) } ?: throw IllegalStateException()

            if (naverProfile.isFail()) {
                throw IllegalStateException()
            }

            val user = userRepository.findByProviderAndProviderId(Provider.NAVER, naverProfile.response.id)
            if (user != null) {
                /* 해당 사용자가 이미 가입한 계정인 경우 */
                throw AlreadyExistedUserException()
            }

            val nickName: String = if (userRepository.findByNickName(naverProfile.response.nickname) != null) {
                Random().ints(97, 123)
                    .limit(10).collect({ StringBuilder() }, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString()
            } else {
                naverProfile.response.nickname
            }

            val newUser = User(
                provider = Provider.NAVER,
                providerId = naverProfile.response.id,
                userEmail = naverProfile.response.email,
                nickName = nickName,
                picture = naverProfile.response.profile_image
            )

            userRepository.save(newUser)

            userLogService.addUserLog(UserLog(user = newUser, logType = LogType.INSERT))

        } else if (provider == Provider.KAKAO.name) {
            val resultMap = findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.KAKAO)
            val kakaoProfile =
                resultMap["access_token"]?.let { findKakaoProfile(it as String) } ?: throw IllegalStateException()

            val user = userRepository.findByProviderAndProviderId(Provider.KAKAO, kakaoProfile.id)

            if (user != null) {
                /* 해당 사용자가 이미 가입한 계정인 경우 */
                throw AlreadyExistedUserException()
            }

            val nickName: String =
                if (userRepository.findByNickName(kakaoProfile.kakaoAccount.profile.nickname) != null) {
                    Random().ints(97, 123)
                        .limit(10).collect({ StringBuilder() }, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
                } else {
                    kakaoProfile.kakaoAccount.profile.nickname
                }

            val newUser = User(
                provider = Provider.KAKAO,
                providerId = kakaoProfile.id,
                userEmail = kakaoProfile.kakaoAccount.email,
                nickName = nickName,
                picture = kakaoProfile.kakaoAccount.profile.profileImageUrl
            )

            userRepository.save(newUser)

            userLogService.addUserLog(UserLog(user = newUser, logType = LogType.INSERT))

        }
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun socialSignIn(request: HashMap<String, String>): UserSignInResponse {
        val provider = request["provider"]
        if (provider == Provider.NAVER.name) {
            val findSocialTokenInfo =
                findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.NAVER)
            val naverProfile = findNaverProfile(findSocialTokenInfo["access_token"].toString())
            if (naverProfile.isFail()) {
                throw IllegalStateException()
            }
            return returnSocialSignIn(
                userRepository.findByProviderAndProviderId(
                    Provider.NAVER,
                    naverProfile.response.id
                )
            )

        } else {
            val findSocialTokenInfo =
                findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.KAKAO)
            val kakaoProfile = findKakaoProfile(findSocialTokenInfo["access_token"].toString())
            return returnSocialSignIn(userRepository.findByProviderAndProviderId(Provider.KAKAO, kakaoProfile.id))
        }
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun returnSocialSignIn(user: User?): UserSignInResponse {
        user?.let {
            val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
            val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
            /* 리프레쉬 토큰 업데이트 */
            user.updateUserLogin(refreshToken)

            /* 유저 로그 INSERT */
            userLogService.addUserLog(UserLog(LocalDateTime.now(), SignInType.SOCIAL, LogType.SIGNIN, user))

            return UserSignInResponse(
                user.userId,
                user.nickName,
                user.userEmail,
                user.picture,
                user.provider,
                accessToken,
                refreshToken,
                user.createdDate,
                LocalDateTime.now()
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun socialMapping(userNo: Long, request: HashMap<String, String>) {
        val provider = request["provider"]
        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()

        if (user.isSocialUser()) {
            throw AlreadyExistedUserException()
        }

        if (provider == Provider.NAVER.name) {
            val socialTokenInfo =
                findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.NAVER)
            val naverProfile = findNaverProfile(socialTokenInfo["access_token"].toString())

            if (!naverProfile.isFail() && !userRepository.existsByProviderIdAndProviderAndDeleteYn(
                    naverProfile.response.id,
                    Provider.NAVER
                )
            ) {
                user.updateUser(
                    provider = Provider.NAVER,
                    providerId = naverProfile.response.id,
                    providerConnectDate = LocalDateTime.now()
                )
                userRepository.flush()
                userLogService.addUserLog(UserLog(logType = LogType.MODIFY, user = user))
            }
        } else {
            val socialTokenInfo =
                findSocialTokenInfo(request["code"].toString(), request["state"].toString(), Provider.KAKAO)
            val kakaoProfile = findKakaoProfile(socialTokenInfo["access_token"].toString())
            if (!userRepository.existsByProviderIdAndProviderAndDeleteYn(kakaoProfile.id, Provider.KAKAO)) {
                user.updateUser(
                    provider = Provider.KAKAO,
                    providerId = kakaoProfile.id,
                    providerConnectDate = LocalDateTime.now()
                )
                userRepository.flush()
                userLogService.addUserLog(UserLog(logType = LogType.MODIFY, user = user))
            }
        }
    }

    fun findUser(userNo: Long): UserDetailResponse? {
        return userRepository.findUserDetail(userNo)
    }

}