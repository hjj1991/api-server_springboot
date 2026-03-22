package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.out.auth.LoadRegisteredSocialProviderPort
import com.hjj.apiserver.domain.auth.AuthProviderType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class SocialAuthProviderServiceTest {
    @Test
    fun `등록된 공급자만 enabled true 로 노출한다`() {
        val loadRegisteredSocialProviderPort = Mockito.mock(LoadRegisteredSocialProviderPort::class.java)
        Mockito.`when`(loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.GOOGLE)).thenReturn(false)
        Mockito.`when`(loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.KAKAO)).thenReturn(true)
        Mockito.`when`(loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.NAVER)).thenReturn(true)
        Mockito.`when`(loadRegisteredSocialProviderPort.isEnabled(AuthProviderType.APPLE)).thenReturn(false)

        val service = SocialAuthProviderService(loadRegisteredSocialProviderPort)

        val result = service.getAvailableProviders()

        assertThat(result).hasSize(4)
        assertThat(result.first { it.providerType == AuthProviderType.KAKAO }.enabled).isTrue()
        assertThat(result.first { it.providerType == AuthProviderType.NAVER }.enabled).isTrue()
        assertThat(result.first { it.providerType == AuthProviderType.GOOGLE }.enabled).isFalse()
        assertThat(result.first { it.providerType == AuthProviderType.APPLE }.authorizationPath)
            .isEqualTo("/oauth2/authorization/apple")
    }
}
