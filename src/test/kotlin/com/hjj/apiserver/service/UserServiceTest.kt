package com.hjj.apiserver.service

import com.hjj.apiserver.application.port.`in`.user.CheckUserNickNameDuplicateCommand
import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.service.UserService
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var getUserPort: GetUserPort

    @Mock
    lateinit var getCredentialPort: GetCredentialPort


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

}