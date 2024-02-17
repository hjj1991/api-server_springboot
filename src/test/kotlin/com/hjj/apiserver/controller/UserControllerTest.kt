package com.hjj.apiserver.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.adapter.input.web.user.UserController
import com.hjj.apiserver.adapter.input.web.user.request.UserSignInRequest
import com.hjj.apiserver.adapter.input.web.user.request.UserSignUpRequest
import com.hjj.apiserver.adapter.input.web.user.response.ExistsNickNameResponse
import com.hjj.apiserver.adapter.input.web.user.response.ExistsUserIdResponse
import com.hjj.apiserver.adapter.input.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.input.user.GetUserUseCase
import com.hjj.apiserver.application.port.input.user.UserCredentialUseCase
import com.hjj.apiserver.application.port.input.user.WriteUserUseCase
import com.hjj.apiserver.application.port.input.user.command.CheckUserNickNameDuplicateCommand
import com.hjj.apiserver.application.port.input.user.command.RegisterUserCommand
import com.hjj.apiserver.application.port.input.user.command.SignInUserCommand
import com.hjj.apiserver.common.ApiError
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.common.exception.AlreadyExistsUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.utils.ApiDocumentUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@DisplayName("유저 API 테스트")
@AutoConfigureRestDocs
@WebMvcTest(UserController::class)
@ImportAutoConfiguration(
    exclude = [OAuth2ClientAutoConfiguration::class],
)
class UserControllerTest {
    companion object {
        const val USER_TAG = "유저 관리 API"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var getUserUseCase: GetUserUseCase

    @MockBean
    private lateinit var userCredentialUseCase: UserCredentialUseCase

    @MockBean
    private lateinit var writeUserUseCase: WriteUserUseCase

    @DisplayName("GET /users/exists-nickname/{nickName} API")
    @Nested
    inner class CheckUserNickNameDuplicateTest {
        @DisplayName("변경할 닉네임이 존재하는지 확인하여 존재하면 true.")
        @Test
        fun checkUserNickNameDuplicateTestWhenexistsnickNameThenTrue() {
            // Given
            val user = getUser()
            val checkUserNickNameDuplicateCommand =
                CheckUserNickNameDuplicateCommand(
                    user,
                    "변경할 닉네임",
                )
            val existsNickNameResponse = ExistsNickNameResponse(true)
            BDDMockito.given(getUserUseCase.existsNickName(checkUserNickNameDuplicateCommand))
                .willReturn(true)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                    "/users/exists-nickname/{nickName}",
                    checkUserNickNameDuplicateCommand.nickName,
                )
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(user)),
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(existsNickNameResponse)),
                )
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "닉네임 존재하는 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("existsNickName").description("해당 닉네임 존재 여부"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("닉네임 존재하는 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("existsNickName").description("해당 닉네임 존재 여부"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtProvider.BEARER_PREFIX + "JWT토큰값",
                                    ).optional(),
                                ).build(),
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtProvider.BEARER_PREFIX + "JWT토큰값").optional(),
                        ),
                    ),
                )
        }

        @DisplayName("유저가 변경할 유저 닉네임이 존재하지 않으면 false")
        @Test
        fun checkUserNickNameDuplicateTest_when_login_user_not_exsists_nickName_then_false() {
            // Given
            val user = getUser()
            val checkUserNickNameDuplicateCommand =
                CheckUserNickNameDuplicateCommand(
                    user,
                    "변경할 닉네임",
                )
            val existsNickNameResponse = ExistsNickNameResponse(false)
            BDDMockito.given(getUserUseCase.existsNickName(checkUserNickNameDuplicateCommand))
                .willReturn(false)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                    "/users/exists-nickname/{nickName}",
                    checkUserNickNameDuplicateCommand.nickName,
                )
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(user)),
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(existsNickNameResponse)),
                )
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "닉네임 존재하지 않는 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("existsNickName").description("해당 닉네임 존재 여부"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("닉네임 존재하지 않는 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("existsNickName").description("해당 닉네임 존재 여부"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtProvider.BEARER_PREFIX + "JWT토큰값",
                                    ).optional(),
                                ).build(),
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtProvider.BEARER_PREFIX + "JWT토큰값").optional(),
                        ),
                    ),
                )
        }
    }

    @DisplayName("GET /users/exists-id/{nickName} API")
    @Nested
    inner class CheckUserIdDuplicateTest {
        @DisplayName("변경할 유저 아이디가 존재하는지 확인하여 존재하면 true.")
        @Test
        @WithMockUser
        fun checkUserIdDuplicateTestWhenExistsUserIdThenTrue() {
            // Given
            val userId = "babo"
            val existsUserIdResponse = ExistsUserIdResponse(true)
            BDDMockito.given(userCredentialUseCase.existsUserId(userId))
                .willReturn(true)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/users/exists-id/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(existsUserIdResponse)))
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "유저 아이디가 존재하는 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("existsUserId").description("해당 아이디 존재 여부"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("아이디가 존재하는 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("existsUserId").description("해당 아이디 존재 여부"),
                                ).build(),
                        ),
                    ),
                )
        }

        @DisplayName("변경할 유저 아이디가 존재하지 않으면 false")
        @Test
        @WithMockUser
        fun checkUserIdDuplicateTestWhenNotExsistsUserIdThenFalse() {
            // Given
            val userId = "noExistsId"
            val existsUserIdResponse = ExistsUserIdResponse(false)
            BDDMockito.given(userCredentialUseCase.existsUserId(userId))
                .willReturn(false)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/users/exists-id/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(existsUserIdResponse)))
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "아이디가 존재하지 않는 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("existsUserId").description("해당 아이디 존재 여부"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("아이디 존재하지 않는 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("existsUserId").description("해당 아이디 존재 여부"),
                                ).build(),
                        ),
                    ),
                )
        }
    }

    @DisplayName("POST /users/signup API")
    @Nested
    inner class SignUpTest {
        @DisplayName("회원가입 성공API")
        @Test
        @WithMockUser
        fun signUpTestSuccess() {
            // Given
            val userSignUpRequest =
                UserSignUpRequest(
                    userId = "1234,",
                    nickName = "nickName",
                    userEmail = "test@example.com",
                    userPw = "1354135",
                )

            val registerUserCommand =
                RegisterUserCommand(
                    userId = userSignUpRequest.userId,
                    nickName = userSignUpRequest.nickName,
                    userEmail = userSignUpRequest.userEmail,
                    userPw = userSignUpRequest.userPw,
                    provider = Provider.GENERAL,
                )
            BDDMockito.doNothing().`when`(writeUserUseCase).signUp(registerUserCommand)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/signup")
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .content(objectMapper.writeValueAsString(userSignUpRequest)),
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "정상적으로 일반 회원가입 완료된 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("정상적으로 일반 회원가입 완료된 경우")
                                .tag(USER_TAG)
                                .build(),
                        ),
                    ),
                )
        }

        @DisplayName("회원가입 실패 API 이미 존재하는 사용자인 경우")
        @Test
        @WithMockUser
        fun signUpTestFailAlreadyExistsUser() {
            // Given
            val userSignUpRequest =
                UserSignUpRequest(
                    userId = "1234,",
                    nickName = "nickName",
                    userEmail = "test@example.com",
                    userPw = "1354135",
                )

            val registerUserCommand =
                RegisterUserCommand(
                    userId = userSignUpRequest.userId,
                    nickName = userSignUpRequest.nickName,
                    userEmail = userSignUpRequest.userEmail,
                    userPw = userSignUpRequest.userPw,
                    provider = Provider.GENERAL,
                )
            BDDMockito.`when`(writeUserUseCase.signUp(registerUserCommand))
                .thenThrow(AlreadyExistsUserException::class.java)

            val alreadyExsistsUserResponse = ApiError(ErrCode.ERR_CODE0006, ErrCode.ERR_CODE0006.msg)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/signup")
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .content(objectMapper.writeValueAsString(userSignUpRequest)),
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(alreadyExsistsUserResponse)),
                )
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "이미 존재하는 사용자인 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("errCode").description("에러코드"),
                            PayloadDocumentation.fieldWithPath("message").description("에러 메시지"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("이미 존재하는 사용자인 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("errCode").description("에러 코드"),
                                    PayloadDocumentation.fieldWithPath("message").description("에러 메시지"),
                                ).build(),
                        ),
                    ),
                )
        }
    }

    @DisplayName("POST /users/signin API")
    @Nested
    inner class SignInTest {
        @DisplayName("로그인 성공 API")
        @Test
        @WithMockUser
        fun signInTestSuccess() {
            // Given
            val userSignInRequest = UserSignInRequest(userId = "1234,", userPw = "<PASSWORD>")

            val signInUserCommand =
                SignInUserCommand(
                    userId = userSignInRequest.userId,
                    userPw = userSignInRequest.userPw,
                    provider = Provider.GENERAL,
                )
            val userSignInResponse =
                UserSignInResponse(
                    nickName = "nickName",
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                )
            BDDMockito.`when`(writeUserUseCase.signIn(signInUserCommand)).thenReturn(userSignInResponse)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/signin")
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .content(objectMapper.writeValueAsString(userSignInRequest)),
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userSignInResponse)),
                )
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "로그인이 정상적으로 성공한 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("nickName").description("닉네임").type(JsonFieldType.STRING),
                            PayloadDocumentation.fieldWithPath("picture").description("프로필 사진")
                                .type(JsonFieldType.NULL),
                            PayloadDocumentation.fieldWithPath("accessToken").description("액세스 토큰")
                                .type(JsonFieldType.STRING),
                            PayloadDocumentation.fieldWithPath("refreshToken").description("리프레쉬 토큰")
                                .type(JsonFieldType.STRING),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("로그인이 정상적으로 성공한 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("nickName").description("닉네임")
                                        .type(JsonFieldType.STRING),
                                    PayloadDocumentation.fieldWithPath("picture").description("프로필 사진")
                                        .type(JsonFieldType.NULL),
                                    PayloadDocumentation.fieldWithPath("accessToken").description("액세스 토큰")
                                        .type(JsonFieldType.STRING),
                                    PayloadDocumentation.fieldWithPath("refreshToken").description("리프레쉬 토큰")
                                        .type(JsonFieldType.STRING),
                                ).build(),
                        ),
                    ),
                )
        }

        @DisplayName("로그인 실패 API")
        @Test
        @WithMockUser
        fun signInTestFailWhenUserNotExists() {
            // Given
            val userSignInRequest = UserSignInRequest(userId = "1234,", userPw = "<PASSWORD>")

            val signInUserCommand =
                SignInUserCommand(
                    userId = userSignInRequest.userId,
                    userPw = userSignInRequest.userPw,
                    provider = Provider.GENERAL,
                )
            val userSignInResponse =
                UserSignInResponse(
                    nickName = "nickName",
                    accessToken = "accessToken",
                    refreshToken = "refreshToken",
                )
            val userNotFoundExceptionResponse = ApiError(ErrCode.ERR_CODE0001, ErrCode.ERR_CODE0001.msg)
            BDDMockito.`when`(writeUserUseCase.signIn(signInUserCommand)).thenThrow(UserNotFoundException::class.java)

            // When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/signin")
                    .header(HttpHeaders.AUTHORIZATION, JwtProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .content(objectMapper.writeValueAsString(userSignInRequest)),
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers.content().json(
                        objectMapper.writeValueAsString(userNotFoundExceptionResponse),
                    ),
                )
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "로그인이 실패한 경우",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("errCode").description("에러 코드"),
                            PayloadDocumentation.fieldWithPath("message").description("에러 메시지"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("로그인이 실패한 경우")
                                .tag(USER_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("errCode").description("에러 코드"),
                                    PayloadDocumentation.fieldWithPath("message").description("에러 메시지"),
                                ).build(),
                        ),
                    ),
                )
        }
    }

    private fun getUser(): User {
        return User(3L, "id", "닉네임", role = Role.USER)
    }
}
