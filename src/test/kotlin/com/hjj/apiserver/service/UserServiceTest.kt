package com.hjj.apiserver.service

import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.dto.user.request.UserModifyRequest
import com.hjj.apiserver.dto.user.request.UserSignInRequest
import com.hjj.apiserver.dto.user.request.UserSinUpRequest
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @BeforeEach
    fun clean() {
        entityManager.createNativeQuery(
            "SET REFERENTIAL_INTEGRITY FALSE; " +
                    "TRUNCATE TABLE tb_category; " +
                    "TRUNCATE TABLE tb_account_book_user; " +
                    "TRUNCATE TABLE tb_account_book; " +
                    "TRUNCATE TABLE tb_purchase; " +
                    "TRUNCATE TABLE tb_user; " +
                    "TRUNCATE TABLE tb_card; " +
                    "SET REFERENTIAL_INTEGRITY TRUE; "
        ).executeUpdate()
    }

    @Test
    @DisplayName("중복 닉네임 체크가 정상 작동한다.")
    fun existsNickName(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        val savedUser = userService.signUp(request)

        // when
        val existsNickName = userService.existsNickName("뜨끔이당")

        // then
        assertThat(existsNickName).isFalse

    }

    @Test
    @DisplayName("중복 아이디 체크가 정상 작동한다.")
    fun existsUserId(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        val savedUser = userService.signUp(request)

        // when
        val existsUserId = userService.existsUserId("testUser12")

        // then
        assertThat(existsUserId).isFalse

    }

    @Test
    @DisplayName("회원가입")
    fun signUpTest(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")

        // when
        val savedUser = userService.signUp(request)


        //then
        val foundUser = userRepository.findByUserId("testUser12")
        assertThat(foundUser!!.nickName).isEqualTo("뜨끔이당")
        assertThat(foundUser!!.userEmail).isEqualTo("testUser@naver.com")
    }

    @Test
    @DisplayName("일반 로그인이 정상 작동한다.")
    fun signInTest(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        userService.signUp(request)

        // when
        val signIn = userService.signIn(
            UserSignInRequest(
                userId = request.userId,
                userPw = request.userPw
            )
        )

        // then
        assertThat(signIn.userId).isEqualTo(request.userId)
        assertThat(signIn.nickName).isEqualTo(request.nickName)
        assertThat(signIn.userEmail).isEqualTo(request.userEmail)
        assertThat(signIn.picture).isNull()
        assertThat(signIn.provider).isNull()
        assertThat(jwtTokenProvider.validateToken(signIn.accessToken)).isTrue
    }

    @Test
    @DisplayName("토큰 재발급이 정상 작동한다.")
    fun reIssueTokenTest(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        userService.signUp(request)
        val signIn = userService.signIn(
            UserSignInRequest(
                userId = request.userId,
                userPw = request.userPw
            )
        )

        // when
        val reIssueToken = userService.reIssueToken(signIn.refreshToken)

        // then
        assertThat(jwtTokenProvider.validateToken(reIssueToken.accessToken)).isTrue
        assertThat(jwtTokenProvider.validateToken(reIssueToken.refreshToken)).isTrue

    }

    @Test
    @DisplayName("사용자 정보가 정상 수정된다.")
    fun modifyUserTest(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        val savedUser = userService.signUp(request)

        // when
        val modifyUser = userService.modifyUser(
            savedUser.userNo!!, UserModifyRequest(
                nickName = "변경닉네임",
                userEmail = "change@email.co.kr",
                userPw = "testPassword12#$"
            )
        )

        // then
        assertThat(modifyUser.nickName).isEqualTo("변경닉네임")
        assertThat(modifyUser.userEmail).isEqualTo("change@email.co.kr")

    }

    @Test
    @DisplayName("사용자 정보가 정상 조회된다.")
    fun findUser(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")
        val savedUser = userService.signUp(request)

        // when
        val userDetailResponse = userService.findUser(savedUser.userNo!!)

        // then
        assertThat(userDetailResponse!!.userId).isEqualTo(request.userId)
        assertThat(userDetailResponse!!.nickName).isEqualTo(request.nickName)
        assertThat(userDetailResponse!!.userEmail).isEqualTo("testUser@naver.com")
    }
}