package com.hjj.apiserver.service

import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.common.TokenType
import com.hjj.apiserver.domain.user.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@ExtendWith(MockitoExtension::class)
class JwtProviderTest {
    private var jwtProvider: JwtProvider

    init {
        val fixedClock = Clock.fixed(Instant.parse("2999-01-01T10:00:00Z"), ZoneOffset.UTC)
        val testSecretKey = "testTokenspaeratkgspeokrtgpaeokrgpaejrgoaeijrgoaeijrgfndkbjnrstghjsrteghjoaeorigjaeorigj"
        jwtProvider = JwtProvider(fixedClock, testSecretKey)
    }

    @Test
    fun createTokenTest_success() {
        // Given
        val user = User(userNo = 11L, nickName = "tester")

        // When
        val token = jwtProvider.createToken(user.userNo, TokenType.ACCESS_TOKEN)

        // Then
        Assertions.assertThat(
            token,
        ).isEqualTo(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsImV4cCI6MzI0NzIxODEyMDAsImlhdCI6MzI0NzIxODAwMDB9.qK" +
                "5PVaTQbrjT1izWB5VZ8JkwRUQlh16Yh7m3a9uq-bLbg5M4R5WrpIZ7Re1i33D90tvLwOCGMYcKSBX3A6Gcvw",
        )
    }

    @Test
    fun isValidTokenTest_success() {
        // Given
        val user = User(userNo = 11L, nickName = "tester")
        val token = jwtProvider.createToken(user.userNo, TokenType.ACCESS_TOKEN)

        // When
        val isValidToken = jwtProvider.isValidToken(token)

        // Then
        Assertions.assertThat(isValidToken).isEqualTo(true)
    }

    @Test
    fun isValidTokenTest_fail_when_expiredToken() {
        // Given
        val user = User(userNo = 11L, nickName = "tester")
        val token = jwtProvider.createToken(user.userNo, TokenType.ACCESS_TOKEN)
        val pastJwtProvider =
            JwtProvider(
                Clock.fixed(Instant.parse("2999-01-01T10:21:00Z"), ZoneOffset.UTC),
                "testTokenspaeratkgspeokrtgpaeokrgpaejrgoaeijrgoaeijrgfndkbjnrstghjsrteghjoaeorigjaeorigj",
            )

        // When
        val isValidToken = pastJwtProvider.isValidToken(token)

        // Then
        Assertions.assertThat(isValidToken).isEqualTo(false)
    }
}
