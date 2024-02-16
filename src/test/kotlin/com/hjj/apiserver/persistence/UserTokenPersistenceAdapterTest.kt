package com.hjj.apiserver.persistence

import com.hjj.apiserver.application.port.out.user.ReadUserTokenPort
import com.hjj.apiserver.application.port.out.user.WriteUserTokenPort
import com.hjj.apiserver.common.exception.TokenNotFoundException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserTokenPersistenceAdapterTest {
    @Autowired
    private lateinit var writeUserTokenPort: WriteUserTokenPort

    @Autowired
    private lateinit var readUserTokenPort: ReadUserTokenPort

    @Test
    fun registerUserTokenTest_success() {
        // Given
        val userNo = 3L
        val refreshToken = "refreshToken"

        // When
        writeUserTokenPort.registerUserToken(userNo, refreshToken)

        // Then
        val findRefreshToken = readUserTokenPort.getUserToken(3L)
        Assertions.assertThat(findRefreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun getUserTokenTest_success() {
        // Given
        val userNo = 3L
        val refreshToken = "refreshToken"

        // When
        writeUserTokenPort.registerUserToken(userNo, refreshToken)

        // Then
        val findRefreshToken = readUserTokenPort.getUserToken(userNo)
        Assertions.assertThat(findRefreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun getUserTokenTest_fail_when_not_exists_key_then_throw_TokenNotFoundException() {
        // Given
        val userNo = 3L
        val refreshToken = "refreshToken"
        writeUserTokenPort.registerUserToken(userNo, refreshToken)

        // When && Then
        Assertions.assertThatThrownBy { readUserTokenPort.getUserToken(135135L) }
            .isInstanceOf(TokenNotFoundException::class.java)
    }

    @Test
    fun deleteUserTokenTest_success() {
        // Given
        val userNo = 3L
        val refreshToken = "refreshToken"
        writeUserTokenPort.registerUserToken(userNo, refreshToken)

        // When
        val isDeleteToken = writeUserTokenPort.deleteUserToken(userNo)

        // Then
        Assertions.assertThat(isDeleteToken).isTrue()
        Assertions.assertThatThrownBy { readUserTokenPort.getUserToken(userNo) }
            .isInstanceOf(TokenNotFoundException::class.java)
    }
}
