package com.hjj.apiserver.service

import com.hjj.apiserver.application.port.`in`.user.command.CheckUserNickNameDuplicateCommand
import com.hjj.apiserver.application.port.`in`.user.command.RegisterUserCommand
import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.WriteCredentialPort
import com.hjj.apiserver.application.port.out.user.WriteUserLogPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserTokenPort
import com.hjj.apiserver.application.service.UserService
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.common.exception.AlreadyExistsUserException
import com.hjj.apiserver.domain.user.Credential
import com.hjj.apiserver.domain.user.CredentialState
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.utils.MockitoTestUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var getUserPort: GetUserPort

    @Mock
    lateinit var getCredentialPort: GetCredentialPort

    @Mock
    lateinit var writeCredentialPort: WriteCredentialPort

    @Mock
    lateinit var writeUserPort: WriteUserPort

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var writeUserLogPort: WriteUserLogPort

    @Mock
    lateinit var writeUserTokenPort: WriteUserTokenPort

    @Mock
    lateinit var jwtProvider: JwtProvider


    @Test
    @DisplayName("닉네임이 이미 존재하는 경우")
    fun existsNickName_when_alreadyExistsNickName_then_true() {
        // given
        val newNickName = "이미 존재하는 닉네임"
        val checkUserNickNameDuplicateCommand = CheckUserNickNameDuplicateCommand(User.createGuestUser(), newNickName)

        Mockito.`when`(getUserPort.findExistsUserNickName(newNickName)).thenReturn(true)


        // when
        val existsNickNameResponse = userService.existsNickName(checkUserNickNameDuplicateCommand)

        // then
        Assertions.assertThat(existsNickNameResponse).isEqualTo(true)
    }

    @Test
    @DisplayName("닉네임이 존재하지 않는 경우")
    fun existsNickName_when_notExistsNickName_then_true() {
        // given
        val newNickName = "존재하지 않는 닉네임"
        val checkUserNickNameDuplicateCommand = CheckUserNickNameDuplicateCommand(User.createGuestUser(), newNickName)

        Mockito.`when`(getUserPort.findExistsUserNickName(newNickName)).thenReturn(false)


        // when
        val existsNickNameResponse = userService.existsNickName(checkUserNickNameDuplicateCommand)

        // then
        Assertions.assertThat(existsNickNameResponse).isEqualTo(false)
    }


    @Test
    @DisplayName("변경하려는 닉네임과 현재 닉네임이 동일한 경우")
    fun existsNickName_when_currentNickNameEqualsNewNickName_then_true() {
        // given
        val newNickName = "이미 존재하는 닉네임"
        val user = User(nickName = newNickName, role = Role.USER)
        val checkUserNickNameDuplicateCommand = CheckUserNickNameDuplicateCommand(user, newNickName)

        // when
        val existsNickNameResponse = userService.existsNickName(checkUserNickNameDuplicateCommand)

        // then
        Assertions.assertThat(existsNickNameResponse).isEqualTo(true)
    }

    @Test
    @DisplayName("회원가입이 정상적으로 성공된다.")
    fun signUp_success_general_user() {
        // Given
        val registerUserCommand = RegisterUserCommand(
            userId = "generalUser",
            nickName = "사이트유저",
            userEmail = "test@Test.com",
            userPw = "testPassword12#$!",
            provider = Provider.GENERAL
        )
        val savedUser = User(
            userNo = 1L,
            nickName = registerUserCommand.nickName,
            userEmail = registerUserCommand.userEmail,
            userPw = registerUserCommand.userPw
        )
        val savedCredential = Credential(
            credentialNo = 1L,
            userId = registerUserCommand.userId,
            credentialEmail = registerUserCommand.userEmail,
            provider = Provider.GENERAL,
            user = savedUser,
            state = CredentialState.CONNECTED,
        )

        Mockito.`when`(writeUserPort.registerUser(MockitoTestUtil.any(User::class.java))).thenReturn(savedUser)
        Mockito.`when`(writeCredentialPort.registerCredential(MockitoTestUtil.any(Credential::class.java))).thenReturn(savedCredential)
        Mockito.`when`(passwordEncoder.encode(registerUserCommand.userPw)).thenReturn("enCryptedPassword")

        // When && Then
        assertDoesNotThrow { userService.signUp(registerUserCommand) }
    }

    @Test
    @DisplayName("동일한 닉네임또는 Credential이 존재하여 insert실패 한 경우 가입이 실패한다.")
    fun signUp_fail_when_duplicateNickName_user_then_throw_already_exists_user_exception() {
        // Given
        val registerUserCommand = RegisterUserCommand(
            userId = "generalUser",
            nickName = "사이트유저",
            userEmail = "test@Test.com",
            userPw = "testPassword12#$!",
            provider = Provider.GENERAL
        )
        val savedUser = User(
            userNo = 1L,
            nickName = registerUserCommand.nickName,
            userEmail = registerUserCommand.userEmail,
            userPw = registerUserCommand.userPw
        )

        Mockito.`when`(writeUserPort.registerUser(MockitoTestUtil.any(User::class.java))).thenThrow(
            DataIntegrityViolationException::class.java
        )
        Mockito.`when`(passwordEncoder.encode(registerUserCommand.userPw)).thenReturn("enCryptedPassword")

        // When && Then
        Assertions.assertThatThrownBy { userService.signUp(registerUserCommand) }
            .isInstanceOf(AlreadyExistsUserException::class.java)
    }

}