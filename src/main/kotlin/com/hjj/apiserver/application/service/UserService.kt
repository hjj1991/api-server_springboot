package com.hjj.apiserver.application.service

import com.hjj.apiserver.application.port.`in`.user.CheckUserNickNameDuplicateCommand
import com.hjj.apiserver.application.port.`in`.user.GetUserUseCase
import com.hjj.apiserver.application.port.`in`.user.RegisterCredentialCommand
import com.hjj.apiserver.application.port.`in`.user.RegisterUserCommand
import com.hjj.apiserver.application.port.`in`.user.UserCredentialUseCase
import com.hjj.apiserver.application.port.`in`.user.WriteUserUseCase
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.common.exception.ProviderNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.util.logger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
//    private val userLogService: UserLogService,
//    private val userLogRepository: UserLogRepository,
//    private val jwtTokenProvider: JwtTokenProvider,
//    private val fireBaseService: FireBaseService,
//    private val objectMapper: ObjectMapper,
    private val userCredentialUseCases: List<UserCredentialUseCase>,
    private val passwordEncoder: PasswordEncoder,
    private val getUserPort: GetUserPort,
    private val writeUserPort: WriteUserPort,

//    @Value(value = "\${app.firebase-storage-uri}")
//    private val firebaseStorageUri: String,
//    @Value(value = "\${app.firebase-bucket}")
//    private val firebaseBucket: String,
) : GetUserUseCase, WriteUserUseCase {
    companion object {
        const val PROFILE_IMG_PATH = "profile/"
    }

    private val log = logger()


    override fun existsNickName(command: CheckUserNickNameDuplicateCommand): kotlin.Boolean {
        if (command.authUser.role != Role.GUEST && command.authUser.nickName == command.nickName) {
            return true
        }
        return getUserPort.findExistsUserNickName(command.nickName)
    }

    @Transactional(readOnly = false)
    override fun signUp(command: RegisterUserCommand) {
        val password = command.userPw?.let { passwordEncoder.encode(it) }
        val user = writeUserPort.registerUser(User(nickName = command.nickName, userEmail = command.userEmail, userPw = password))

        val registerCredentialCommand = RegisterCredentialCommand(
            userId = command.userId,
            user = user,
            userEmail = command.userEmail,
            provider = command.provider
        )
        val matchingProviderCredentialService = getMatchingProviderCredentialService(command.provider)
        val credential = matchingProviderCredentialService.register(registerCredentialCommand)
//        for (authService in userCredentialServices) {
//            if (authService.isMatchingProvider(userAttribute.provider)) {
//                val newUser = authService.register(userAttribute)
//                val savedUser = userRepository.save(newUser)
//                userLogRepository.save(UserLog(logType = LogType.INSERT, userEntity = savedUser))
//                return
//            }
//        }
    }

//    fun existsUserId(userId: String): Boolean {
//        return userRepository.findExistsUserId(userId)
//    }
//

    //
//    @Transactional(readOnly = false, rollbackFor = [Exception::class])
//    fun signIn(userAttribute: UserAttribute): UserSignInResponse {
//        for (authService in authServices) {
//            if(authService.isMatchingProvider(userAttribute.provider)){
//                val user = authService.signIn(userAttribute)
//                val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
//                val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
//                /* 리프레쉬 토큰 업데이트 */
//                user.updateUserLogin(refreshToken)
//                /* 유저 로그 INSERT */
//                userLogService.addUserLog(UserLog(LocalDateTime.now(), LogType.SIGNIN, user))
//                return UserSignInResponse.of(user, accessToken, refreshToken)
//            }
//        }
//
//        throw Exception()
//    }
//
//    @Transactional(readOnly = false, rollbackFor = [Exception::class])
//    fun reIssueToken(refreshToken: String): UserReIssueTokenResponse {
//        if (!jwtTokenProvider.validateToken(refreshToken)) {
//            throw IllegalAccessException()
//        }
//
//        val user = userRepository.findByRefreshToken(refreshToken) ?: throw UserNotFoundException()
//
//        val newRefreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
//        user.updateUserLogin(newRefreshToken)
//
//        return UserReIssueTokenResponse(jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN), newRefreshToken)
//    }
//
//
//    @Transactional(readOnly = false, rollbackFor = [Exception::class])
//    fun modifyUser(userNo: Long, request: UserModifyRequest): UserSignInResponse {
//
//        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()
//        if (user.provider == null && !passwordEncoder.matches(request.userPw, user.userPw)) {
//            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
//        }
//
//        user.updateUser(request.nickName, request.userEmail)
//        userRepository.flush()
//        userLogService.addUserLog(UserLog(logType = LogType.MODIFY, userEntity = user))
//
//
//        val accessToken = jwtTokenProvider.createToken(user, TokenType.ACCESS_TOKEN)
//        val refreshToken = jwtTokenProvider.createToken(user, TokenType.REFRESH_TOKEN)
//        /* 리프레쉬 토큰 업데이트 */
//        user.updateUserLogin(refreshToken)
//
//        return UserSignInResponse(
//            user.userId,
//            user.nickName,
//            user.userEmail,
//            user.picture,
//            user.provider,
//            accessToken,
//            refreshToken,
//            user.createdAt
//        )
//    }
//
//
//    @Transactional(readOnly = false, rollbackFor = [Exception::class])
//    fun modifyUserPicture(userNo: Long, pictureFile: MultipartFile) {
//        /*  이미지 썸네일 제작 프론트에서 처리하도록 수정
//        val bufferedImage = ImageIO.read(pictureFile.inputStream)
//        val imgWidth = bufferedImage.height.coerceAtMost(bufferedImage.width)
//        val scaledImg = Scalr.crop(
//            bufferedImage,
//            (bufferedImage.width - imgWidth) / 2,
//            (bufferedImage.height - imgWidth) / 2,
//            imgWidth,
//            imgWidth,
//            null
//        )
//        val resizedImg = Scalr.resize(scaledImg, 100, 100, null)
//        */
//        /* outputStream에 image객체 저장 *//*
//
//        ImageIO.write(resizedImg, "jpg", ByteArrayOutputStream())
//        */
//
//        val user = userRepository.findByIdOrNull(userNo) ?: throw UserNotFoundException()
//
//        val fileName = PROFILE_IMG_PATH + user.userNo + ".png"
//        fireBaseService.putProfileImg(pictureFile.bytes, fileName)
//        val picturePath =
//            firebaseStorageUri + firebaseBucket + "/o/" + URLEncoder.encode(fileName, "UTF-8") + "?alt=media"
//        user.updateUser(picture = picturePath)
//    }
//
//
//    fun findUser(userNo: Long): UserDetailResponse? {
//        return userRepository.findUserDetail(userNo)
//    }
//
//    @Transactional(readOnly = false, rollbackFor = [Exception::class])
//    fun socialMapping(oAuth2User: OAuth2User) {
//        val user = userRepository.findByIdOrNull(oAuth2User.attributes["mappingUserNo"] as Long)?: throw UserNotFoundException()
//        if(user.isSocialUser()){
//            throw AlreadyExistedUserException()
//        }
//
//        val oAuth2UserAttribute = objectMapper.convertValue(oAuth2User.attributes, OAuth2UserAttribute::class.java)
//
////        user.updateUser(
////            provider = oAuth2UserAttribute.provider,
////            providerId = oAuth2UserAttribute.providerId,
////            providerConnectDate = LocalDateTime.now()
////        )
//
//        userRepository.flush()
//        userLogService.addUserLog(UserLog(logType = LogType.MODIFY, userEntity = user))
//
//    }
    private fun getMatchingProviderCredentialService(provider: Provider): UserCredentialUseCase {
        for (userCredentialService in userCredentialUseCases) {
            if(userCredentialService.isMatchingProvider(provider)){
                return userCredentialService
            }
        }
        throw ProviderNotFoundException()
    }
}