package com.hjj.apiserver.adapter.out.security.auth.social

import com.hjj.apiserver.domain.auth.AuthProviderType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DelegatingSocialProviderProfileExtractorTest {
    private val extractor =
        DelegatingSocialProviderProfileExtractor(
            listOf(
                GoogleSocialProviderProfileExtractor(),
                KakaoSocialProviderProfileExtractor(),
                NaverSocialProviderProfileExtractor(),
                AppleSocialProviderProfileExtractor(),
            ),
        )

    @Test
    fun `카카오 attributes 를 표준 profile 로 변환한다`() {
        val result =
            extractor.extract(
                "kakao",
                mapOf(
                    "id" to 12345L,
                    "kakao_account" to
                        mapOf(
                            "email" to "hello@example.com",
                            "is_email_verified" to true,
                            "profile" to mapOf("nickname" to "카카오 사용자"),
                        ),
                ),
            )

        assertThat(result.providerType).isEqualTo(AuthProviderType.KAKAO)
        assertThat(result.providerSubject).isEqualTo("12345")
        assertThat(result.displayName).isEqualTo("카카오 사용자")
        assertThat(result.email).isEqualTo("hello@example.com")
        assertThat(result.emailVerified).isTrue()
    }

    @Test
    fun `네이버 attributes 를 표준 profile 로 변환한다`() {
        val result =
            extractor.extract(
                "naver",
                mapOf(
                    "response" to
                        mapOf(
                            "id" to "naver-subject",
                            "name" to "네이버 사용자",
                            "email" to "naver@example.com",
                        ),
                ),
            )

        assertThat(result.providerType).isEqualTo(AuthProviderType.NAVER)
        assertThat(result.providerSubject).isEqualTo("naver-subject")
        assertThat(result.displayName).isEqualTo("네이버 사용자")
        assertThat(result.email).isEqualTo("naver@example.com")
        assertThat(result.emailVerified).isTrue()
    }
}
