package com.hjj.apiserver.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.common.TokenType
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.LogType
import com.hjj.apiserver.domain.user.SignInType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.dto.oauth2.OAuth2Attribute
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.dto.user.UserAttribute
import com.hjj.apiserver.dto.user.request.UserModifyRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.request.UserSignUpRequest
import com.hjj.apiserver.dto.user.response.UserDetailResponse
import com.hjj.apiserver.dto.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.dto.user.response.UserSignInResponse
import com.hjj.apiserver.repository.user.UserLogRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userLogService: UserLogService,
    private val userLogRepository: UserLogRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fireBaseService: FireBaseService,
    private val objectMapper: ObjectMapper,
    private val authServices: List<UserAuthService>,
    private val passwordEncoder: PasswordEncoder,


    @Value(value = "\${app.firebase-storage-uri}")
    private val firebaseStorageUri: String,
    @Value(value = "\${app.firebase-bucket}")
    private val firebaseBucket: String,
) {
    companion object {
        const val PROFILE_IMG_PATH = "profile/"
    }

    private val log = logger()


    fun existsNickName(currentUserInfo: CurrentUserInfo?, nickName: String): Boolean {
        /* 자기자신의 닉네임과 동일 한 경우 true 리턴 */
        return if (currentUserInfo?.nickName == nickName) {
            true
        } else {
            userRepository.findExistsUserNickName(nickName)
        }
    }

    fun existsUserId(userId: String): Boolean {
        return userRepository.findExistsUserId(userId)
    }

    @Transactional(readOnly = false)
    fun signUp(userAttribute: UserAttribute) {
        for (authService in authServices) {
            if(authService.isMatchingProvider(userAttribute.provider)){
                val newUser = authService.register(userAttribute)
                val savedUser = userRepository.save(newUser)
                userLogRepository.save(UserLog(logType = LogType.INSERT, user = savedUser))
                return
            }
        }
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun signIn(userAttribute: UserAttribute): UserSignInResponse {
        for (authService in authServices) {
            if(authService.isMatchingProvider(userAttribute.provider)){
                val user = authService.signIn(userAttribute)
                val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
                val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
                /* 리프레쉬 토큰 업데이트 */
                user.updateUserLogin(refreshToken)
                /* 유저 로그 INSERT */
                userLogService.addUserLog(UserLog(LocalDateTime.now(), SignInType.GENERAL, LogType.SIGNIN, user))
                return UserSignInResponse.of(user, accessToken, refreshToken)
            }
        }

        throw Exception()
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


    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyUser(userNo: Long, request: UserModifyRequest): UserSignInResponse {

        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()
        if (user.provider == null && !passwordEncoder.matches(request.userPw, user.userPw)) {
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
            user.createdAt
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


    fun findUser(userNo: Long): UserDetailResponse? {
        return userRepository.findUserDetail(userNo)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun socialMapping(oAuth2User: OAuth2User) {
        val user = userRepository.findByIdOrNull(oAuth2User.attributes["mappingUserNo"] as Long)?: throw UserNotFoundException()
        if(user.isSocialUser()){
            throw AlreadyExistedUserException()
        }

        val oAuth2Attribute = objectMapper.convertValue(oAuth2User.attributes, OAuth2Attribute::class.java)

        user.updateUser(
            provider = oAuth2Attribute.provider,
            providerId = oAuth2Attribute.providerId,
            providerConnectDate = LocalDateTime.now()
        )

        userRepository.flush()
        userLogService.addUserLog(UserLog(logType = LogType.MODIFY, user = user))

    }

}