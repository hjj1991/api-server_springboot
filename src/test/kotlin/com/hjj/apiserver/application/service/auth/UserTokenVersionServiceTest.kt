package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.out.auth.IncreaseStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.LoadStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.UserTokenVersionCachePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Duration

class UserTokenVersionServiceTest {
    private lateinit var loadStoredUserTokenVersionPort: LoadStoredUserTokenVersionPort
    private lateinit var increaseStoredUserTokenVersionPort: IncreaseStoredUserTokenVersionPort
    private lateinit var userTokenVersionCachePort: UserTokenVersionCachePort
    private lateinit var authProperties: AuthProperties
    private lateinit var userTokenVersionService: UserTokenVersionService

    @BeforeEach
    fun setUp() {
        loadStoredUserTokenVersionPort = Mockito.mock(LoadStoredUserTokenVersionPort::class.java)
        increaseStoredUserTokenVersionPort = Mockito.mock(IncreaseStoredUserTokenVersionPort::class.java)
        userTokenVersionCachePort = Mockito.mock(UserTokenVersionCachePort::class.java)
        authProperties = AuthProperties(tokenVersionCacheTtl = Duration.ofHours(12))

        userTokenVersionService =
            UserTokenVersionService(
                loadStoredUserTokenVersionPort = loadStoredUserTokenVersionPort,
                increaseStoredUserTokenVersionPort = increaseStoredUserTokenVersionPort,
                userTokenVersionCachePort = userTokenVersionCachePort,
                authProperties = authProperties,
            )
    }

    @Test
    fun `캐시에 token version 이 있으면 저장소를 조회하지 않는다`() {
        Mockito.`when`(userTokenVersionCachePort.get(1L)).thenReturn(3L)

        val tokenVersion = userTokenVersionService.getCurrentTokenVersion(1L)

        assertThat(tokenVersion).isEqualTo(3L)
        Mockito.verifyNoInteractions(loadStoredUserTokenVersionPort)
    }

    @Test
    fun `캐시 miss 이면 저장소 조회 후 캐시에 채운다`() {
        Mockito.`when`(userTokenVersionCachePort.get(2L)).thenReturn(null)
        Mockito.`when`(loadStoredUserTokenVersionPort.loadTokenVersion(2L)).thenReturn(5L)

        val tokenVersion = userTokenVersionService.getCurrentTokenVersion(2L)

        assertThat(tokenVersion).isEqualTo(5L)
        Mockito.verify(userTokenVersionCachePort).set(2L, 5L, Duration.ofHours(12))
    }

    @Test
    fun `token version 증가시 저장소와 캐시를 함께 갱신한다`() {
        Mockito.`when`(increaseStoredUserTokenVersionPort.increaseTokenVersion(10L)).thenReturn(3L)

        val increasedVersion = userTokenVersionService.increaseTokenVersion(10L)

        assertThat(increasedVersion).isEqualTo(3L)
        Mockito.verify(userTokenVersionCachePort).set(10L, 3L, Duration.ofHours(12))
    }
}
