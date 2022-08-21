package com.hjj.apiserver.service.user

import com.hjj.apiserver.domain.user.LogType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.dto.user.request.UserAddRequest
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
) {
    private val log = logger()

    @Value(value = "\${social.naver.url.token.host}")
    lateinit var naverTokenHost: String
    @Value(value = "\${social.naver.url.token.path}")
    lateinit var naverTokenPath: String
    @Value(value = "\${social.naver.url.profile.host}")
    lateinit var naverProfileHost: String
    @Value(value = "\${social.naver.url.profile.path}")
    lateinit var naverProfilePath: String
    @Value(value = "\${social.naver.client-id}")
    lateinit var naverClientId: String
    @Value(value = "\${social.naver.client-secret}")
    lateinit var naverClientSecret: String
    @Value(value = "\${social.kakao.url.profile.host}")
    lateinit var kakaoProfileHost: String
    @Value(value = "\${social.kakao.url.profile.path}")
    lateinit var kakaoProfilePath: String
    @Value(value = "\${social.kakao.url.token.host}")
    lateinit var kakaoTokenHost: String
    @Value(value = "\${social.kakao.url.token.path}")
    lateinit var kakaoTokenPath: String
    @Value(value = "\${social.kakao.client-id}")
    lateinit var kakaoClientId: String
    @Value(value = "\${social.kakao.client-secret}")
    lateinit var kakaoClientSecret: String
    @Value(value = "\${app.firebase-storage-uri}")
    lateinit var firebaseStorageUri: String
    @Value(value = "\${app.firebase-bucket}")
    lateinit var firebaseBucket: String

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


}