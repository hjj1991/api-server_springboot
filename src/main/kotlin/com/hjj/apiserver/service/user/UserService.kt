package com.hjj.apiserver.service.user

import com.hjj.apiserver.domain.user.LogType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.dto.user.request.UserAddRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.repository.user.UserLogRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userLogRepository: UserLogRepository,

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
    fun signIn(request: UserSignInRequest) {

    }


}