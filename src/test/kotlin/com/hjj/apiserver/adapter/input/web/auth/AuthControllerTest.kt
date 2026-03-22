package com.hjj.apiserver.adapter.input.web.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.port.input.auth.AuthSessionResult
import com.hjj.apiserver.application.port.input.auth.GetSocialAuthProvidersUseCase
import com.hjj.apiserver.application.port.input.auth.LocalLoginCommand
import com.hjj.apiserver.application.port.input.auth.ManageAuthSessionUseCase
import com.hjj.apiserver.application.port.input.auth.LocalSignupCommand
import com.hjj.apiserver.application.port.input.auth.ManageSocialAuthUseCase
import com.hjj.apiserver.application.port.input.auth.RefreshSessionCommand
import com.hjj.apiserver.application.port.input.auth.ResolveSocialLoginCommand
import com.hjj.apiserver.application.port.input.auth.RegisterLocalSignupUseCase
import com.hjj.apiserver.application.port.input.auth.SignupAcceptedResult
import com.hjj.apiserver.application.port.input.auth.SignupVerifiedResult
import com.hjj.apiserver.application.port.input.auth.SocialAuthProviderResult
import com.hjj.apiserver.application.port.input.auth.SocialAccountResolutionType
import com.hjj.apiserver.application.port.input.auth.SocialLoginResolutionResult
import com.hjj.apiserver.application.port.input.auth.VerifySignupCommand
import com.hjj.apiserver.common.ApiProblemFactory
import com.hjj.apiserver.common.ExceptionControllerAdvice
import com.hjj.apiserver.config.ErrorResponseProperties
import com.hjj.apiserver.domain.auth.AuthProviderType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.time.OffsetDateTime

class AuthControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper
    private lateinit var registerLocalSignupUseCase: RegisterLocalSignupUseCase
    private lateinit var manageAuthSessionUseCase: ManageAuthSessionUseCase
    private lateinit var manageSocialAuthUseCase: ManageSocialAuthUseCase
    private lateinit var getSocialAuthProvidersUseCase: GetSocialAuthProvidersUseCase

    @BeforeEach
    fun setUp() {
        registerLocalSignupUseCase = Mockito.mock(RegisterLocalSignupUseCase::class.java)
        manageAuthSessionUseCase = Mockito.mock(ManageAuthSessionUseCase::class.java)
        manageSocialAuthUseCase = Mockito.mock(ManageSocialAuthUseCase::class.java)
        getSocialAuthProvidersUseCase = Mockito.mock(GetSocialAuthProvidersUseCase::class.java)
        objectMapper = ObjectMapper().findAndRegisterModules()

        val errorResponseProperties = ErrorResponseProperties().apply {
            problemTypeBaseUri = "https://api.test.local/problems"
        }

        mockMvc =
            MockMvcBuilders.standaloneSetup(
                AuthController(
                    registerLocalSignupUseCase = registerLocalSignupUseCase,
                    manageAuthSessionUseCase = manageAuthSessionUseCase,
                    manageSocialAuthUseCase = manageSocialAuthUseCase,
                    getSocialAuthProvidersUseCase = getSocialAuthProvidersUseCase,
                ),
            )
                .setValidator(
                    LocalValidatorFactoryBean().apply {
                        afterPropertiesSet()
                    },
                )
                .setControllerAdvice(ExceptionControllerAdvice(ApiProblemFactory(errorResponseProperties)))
                .build()
    }

    @Test
    fun `로그인 성공시 access 와 refresh 토큰을 반환한다`() {
        Mockito.doReturn(
            AuthSessionResult(
                accessToken = "access-token",
                accessTokenExpiresAt = OffsetDateTime.parse("2026-03-22T00:10:00Z"),
                refreshToken = "refresh-token",
                refreshTokenExpiresAt = OffsetDateTime.parse("2026-04-05T00:00:00Z"),
            ),
        ).`when`(manageAuthSessionUseCase).login(
            LocalLoginCommand(
                loginId = "hello-user",
                password = "password123",
            ),
        )

        mockMvc.perform(
            post("/auth/login")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "loginId" to "hello-user",
                            "password" to "password123",
                        ),
                    ),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
    }

    @Test
    fun `리프레시 성공시 새 세션 토큰을 반환한다`() {
        Mockito.doReturn(
            AuthSessionResult(
                accessToken = "new-access-token",
                accessTokenExpiresAt = OffsetDateTime.parse("2026-03-22T00:20:00Z"),
                refreshToken = "new-refresh-token",
                refreshTokenExpiresAt = OffsetDateTime.parse("2026-04-05T00:10:00Z"),
            ),
        ).`when`(manageAuthSessionUseCase).refresh(
            RefreshSessionCommand(
                refreshToken = "refresh-token",
            ),
        )

        mockMvc.perform(
            post("/auth/refresh")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "refreshToken" to "refresh-token",
                        ),
                    ),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
    }

    @Test
    fun `로컬 가입 요청 성공시 202 Accepted 를 반환한다`() {
        Mockito.doReturn(
            SignupAcceptedResult(
                email = "hello@example.com",
                expiresAt = OffsetDateTime.parse("2026-03-22T01:30:00Z"),
            )
        ).`when`(registerLocalSignupUseCase).signup(
            LocalSignupCommand(
                displayName = "홍길동",
                loginId = "hello-user",
                email = "hello@example.com",
                password = "password123",
            ),
        )

        mockMvc.perform(
            post("/auth/signup")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "displayName" to "홍길동",
                            "loginId" to "hello-user",
                            "email" to "hello@example.com",
                            "password" to "password123",
                        ),
                    ),
                ),
        )
            .andExpect(status().isAccepted)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("hello@example.com"))
            .andExpect(jsonPath("$.expiresAt").value("2026-03-22T01:30:00Z"))
    }

    @Test
    fun `이메일 형식이 잘못되면 400 ProblemDetail 을 반환한다`() {
        mockMvc.perform(
            post("/auth/signup")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "displayName" to "홍길동",
                            "loginId" to "hello-user",
                            "email" to "not-an-email",
                            "password" to "password123",
                        ),
                    ),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("ERR_CODE0016"))
            .andExpect(jsonPath("$.details[0].field").value("email"))

        Mockito.verifyNoInteractions(registerLocalSignupUseCase)
    }

    @Test
    fun `가입 인증 성공시 201 Created 를 반환한다`() {
        Mockito.doReturn(
            SignupVerifiedResult(
                email = "hello@example.com",
                verifiedAt = OffsetDateTime.parse("2026-03-22T02:00:00Z"),
            )
        ).`when`(registerLocalSignupUseCase).verifySignup(
            VerifySignupCommand(
                token = "verification-token",
            ),
        )

        mockMvc.perform(
            post("/auth/signup/verify")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "token" to "verification-token",
                        ),
                    ),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value("hello@example.com"))
            .andExpect(jsonPath("$.verifiedAt").value("2026-03-22T02:00:00Z"))
    }

    @Test
    fun `소셜 공급자 목록을 반환한다`() {
        Mockito.doReturn(
            listOf(
                SocialAuthProviderResult(
                    providerType = AuthProviderType.KAKAO,
                    displayName = "Kakao",
                    enabled = true,
                    authorizationPath = "/oauth2/authorization/kakao",
                ),
            ),
        ).`when`(getSocialAuthProvidersUseCase).getAvailableProviders()

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/auth/social/providers")
                .header("API-Version", "1.0"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].providerType").value("KAKAO"))
            .andExpect(jsonPath("$[0].enabled").value(true))
            .andExpect(jsonPath("$[0].authorizationPath").value("/oauth2/authorization/kakao"))
    }

    @Test
    fun `소셜 로그인 resolve 성공시 인증 세션을 반환한다`() {
        Mockito.doReturn(
            SocialLoginResolutionResult.Authenticated(
                authSession =
                    AuthSessionResult(
                        accessToken = "social-access-token",
                        accessTokenExpiresAt = OffsetDateTime.parse("2026-03-22T00:10:00Z"),
                        refreshToken = "social-refresh-token",
                        refreshTokenExpiresAt = OffsetDateTime.parse("2026-04-05T00:00:00Z"),
                    ),
                userId = 10L,
                resolutionType = SocialAccountResolutionType.CREATED_NEW_USER,
            ),
        ).`when`(manageSocialAuthUseCase).resolveLogin(
            ResolveSocialLoginCommand(
                providerType = AuthProviderType.GOOGLE,
                providerSubject = "subject-1",
                displayName = "구글 사용자",
                email = "hello@example.com",
                emailVerified = true,
            ),
        )

        mockMvc.perform(
            post("/auth/social/resolve")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "providerType" to "GOOGLE",
                            "providerSubject" to "subject-1",
                            "displayName" to "구글 사용자",
                            "email" to "hello@example.com",
                            "emailVerified" to true,
                        ),
                    ),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultType").value("AUTHENTICATED"))
            .andExpect(jsonPath("$.userId").value(10))
            .andExpect(jsonPath("$.resolutionType").value("CREATED_NEW_USER"))
            .andExpect(jsonPath("$.authSession.accessToken").value("social-access-token"))
    }

    @Test
    fun `소셜 로그인 resolve 에서 연결 필요 응답을 반환할 수 있다`() {
        Mockito.doReturn(
            SocialLoginResolutionResult.RequiresLink(
                providerType = AuthProviderType.KAKAO,
                normalizedEmail = "hello@example.com",
            ),
        ).`when`(manageSocialAuthUseCase).resolveLogin(
            ResolveSocialLoginCommand(
                providerType = AuthProviderType.KAKAO,
                providerSubject = "subject-2",
                displayName = "카카오 사용자",
                email = "hello@example.com",
                emailVerified = true,
            ),
        )

        mockMvc.perform(
            post("/auth/social/resolve")
                .header("API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "providerType" to "KAKAO",
                            "providerSubject" to "subject-2",
                            "displayName" to "카카오 사용자",
                            "email" to "hello@example.com",
                            "emailVerified" to true,
                        ),
                    ),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultType").value("REQUIRES_LINK"))
            .andExpect(jsonPath("$.providerType").value("KAKAO"))
            .andExpect(jsonPath("$.normalizedEmail").value("hello@example.com"))
    }
}
