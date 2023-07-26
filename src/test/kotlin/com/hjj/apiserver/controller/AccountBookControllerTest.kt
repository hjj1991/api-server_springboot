package com.hjj.apiserver.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookAddResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.dto.category.CategoryDto
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.AccountBookService
import com.hjj.apiserver.utils.ApiDocumentUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@DisplayName("가계부 관리 API 테스트")
@AutoConfigureRestDocs
@WebMvcTest(AccountBookController::class)
@ImportAutoConfiguration(
    exclude = [OAuth2ClientAutoConfiguration::class]
)
class AccountBookControllerTest {

    private val ACCOUNTBOOK_TAG = "가계부 관리 API"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var accountBookService: AccountBookService

    @DisplayName("GET /account-books API 성공 200")
    @Nested
    inner class AccountBooks_get_success {
        @DisplayName("개인의 가계부 전체를 가져온다.")
        @Test
        fun accountBooksFind_success() {
            // Given
            val accountBookFindAllResponse1 = AccountBookFindAllResponse(
                accountBookNo = 1L,
                accountBookName = "가계부1",
                accountBookDesc = "설명1",
                backGroundColor = "#00000",
                color = "#11111",
                accountRole = AccountRole.OWNER,
                joinedUsers = listOf(AccountBookFindAllResponse.JoinedUser(userNo = 1L, nickName = "닉네임", picture = ""))
            )
            val accountBookFindAllResponse2 = AccountBookFindAllResponse(
                accountBookNo = 2L,
                accountBookName = "가계부2",
                accountBookDesc = "설명2",
                backGroundColor = "#00000",
                color = "#11111",
                accountRole = AccountRole.OWNER,
                joinedUsers = listOf(AccountBookFindAllResponse.JoinedUser(userNo = 1L, nickName = "닉네임", picture = ""))
            )
            val accountBookFindAllResponse = listOf(accountBookFindAllResponse1, accountBookFindAllResponse2)

            BDDMockito.given(accountBookService.findAllAccountBook(ArgumentMatchers.anyLong()))
                .willReturn(accountBookFindAllResponse)

            val userInfo = getUserInfo()

            // When && Then
            mockMvc.perform(
                get("/account-books")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(userInfo))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "가계부 목록 조회 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("[].accountBookNo").description("가계부 No"),
                            PayloadDocumentation.fieldWithPath("[].accountBookName").description("가계부명"),
                            PayloadDocumentation.fieldWithPath("[].accountBookDesc").description("가계부 설명"),
                            PayloadDocumentation.fieldWithPath("[].backGroundColor").description("가계부 배경색"),
                            PayloadDocumentation.fieldWithPath("[].color").description("가계부 글씨색"),
                            PayloadDocumentation.fieldWithPath("[].accountRole").description("해당 가계부 사용자권한"),
                            PayloadDocumentation.fieldWithPath("[].joinedUsers").description("해당 가계부에 속한 사용자 상세정보"),
                            PayloadDocumentation.fieldWithPath("[].joinedUsers.[].userNo")
                                .description("해당 가계부에 속한 사용자번호"),
                            PayloadDocumentation.fieldWithPath("[].joinedUsers.[].nickName")
                                .description("해당 가계부에 속한 사용자별명"),
                            PayloadDocumentation.fieldWithPath("[].joinedUsers.[].picture")
                                .description("해당 가계부에 속한 사용자사진"),
                        ),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 가계부 목록이 정상 조회됩니다.")
                                .tag(ACCOUNTBOOK_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("[].accountBookNo").description("가계부 No"),
                                    PayloadDocumentation.fieldWithPath("[].accountBookName").description("가계부명"),
                                    PayloadDocumentation.fieldWithPath("[].accountBookDesc").description("가계부 설명"),
                                    PayloadDocumentation.fieldWithPath("[].backGroundColor").description("가계부 배경색"),
                                    PayloadDocumentation.fieldWithPath("[].color").description("가계부 글씨색"),
                                    PayloadDocumentation.fieldWithPath("[].accountRole").description("해당 가계부 사용자권한"),
                                    PayloadDocumentation.fieldWithPath("[].joinedUsers").description("해당 가계부에 속한 사용자 상세정보"),
                                    PayloadDocumentation.fieldWithPath("[].joinedUsers.[].userNo")
                                        .description("해당 가계부에 속한 사용자번호"),
                                    PayloadDocumentation.fieldWithPath("[].joinedUsers.[].nickName")
                                        .description("해당 가계부에 속한 사용자별명"),
                                    PayloadDocumentation.fieldWithPath("[].joinedUsers.[].picture")
                                        .description("해당 가계부에 속한 사용자사진"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                ).build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }

    }

    @DisplayName("POST /account-books API 성공 201")
    @Nested
    inner class AccountBooks_post_success {
        @DisplayName("개인의 가계부를 등록한다.")
        @Test
        fun accountBooksAdd_success() {
            // Given
            val accountBookAddRequest = AccountBookAddRequest(
                accountBookName = "가계부명",
                accountBookDesc = "가계부설명",
                backGroundColor = "#00000",
                color = "#11111"
            )

            val accountBook = AccountBook(
                accountBookNo = 1L,
                accountBookName = accountBookAddRequest.accountBookName,
                accountBookDesc = accountBookAddRequest.accountBookDesc
            )


            val userInfo = getUserInfo()

            val accountBookUser = AccountBookUser(
                accountBookUserNo = userInfo.userNo,
                accountBook = accountBook,
                user = getUser(getUserInfo()),
                accountRole = AccountRole.OWNER,
                backGroundColor = accountBookAddRequest.backGroundColor,
                color = accountBookAddRequest.color
            )


            BDDMockito.given(accountBookService.addAccountBook(userInfo.userNo, accountBookAddRequest))
                .willReturn(AccountBookAddResponse.of(accountBookUser))


            // When && Then
            mockMvc.perform(
                post("/account-books")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(accountBookAddRequest))
                    .with(SecurityMockMvcRequestPostProcessors.user(userInfo))
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "가계부 등록 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 가계부 목록이 정상 등록됩니다.")
                                .tag(ACCOUNTBOOK_TAG)
                                .requestFields(
                                    PayloadDocumentation.fieldWithPath("accountBookName").description("가계부명"),
                                    PayloadDocumentation.fieldWithPath("accountBookDesc").description("가계부 설명"),
                                    PayloadDocumentation.fieldWithPath("backGroundColor").description("가계부 배경색"),
                                    PayloadDocumentation.fieldWithPath("color").description("가계부 글씨색"),
                                )
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("accountBookNo").description("가계부 No"),
                                    PayloadDocumentation.fieldWithPath("accountBookName").description("가계부명"),
                                    PayloadDocumentation.fieldWithPath("accountBookDesc").description("가계부 설명"),
                                    PayloadDocumentation.fieldWithPath("backGroundColor").description("가계부 배경색"),
                                    PayloadDocumentation.fieldWithPath("color").description("가계부 글씨색"),
                                    PayloadDocumentation.fieldWithPath("accountRole").description("해당 가계부 사용자권한"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                ).build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }
    }

    @DisplayName("GET /account-books/{accountBookNo} API")
    @Nested
    inner class AccountBooksDetail_get {
        @DisplayName("개인의 가계부 상세정보를 가져온다. 성공 200")
        @Test
        fun accountBooksDetail_success() {
            // Given
            val createAt = LocalDateTime.of(2023, 4, 10, 10, 0, 0)

            val cards = listOf(
                AccountBookDetailResponse.CardDetail(
                    cardNo = 1L,
                    cardName = "신한카드",
                    cardType = CardType.CREDIT_CARD,
                    cardDesc = "혜택가득"
                )
            )

            val childCategories = mutableListOf(
                CategoryDto.ChildCategory(
                    categoryNo = 2L,
                    categoryName = "자식카테고리",
                    categoryDesc = "자식카테고리 설명",
                    categoryIcon = "자식카테고리 아이콘",
                    accountBookNo = 1L,
                    parentCategoryNo = 1L
                )
            )

            val categories = listOf(
                CategoryDto(
                    categoryNo = 1L,
                    categoryName = "금융",
                    categoryDesc = "설명",
                    categoryIcon = "아이콘",
                    accountBookNo = 1L,
                    childCategories = childCategories
                )
            )

            val accountBookDetailResponse = AccountBookDetailResponse(
                accountBookNo = 1L,
                accountBookName = "가계부1",
                accountBookDesc = "설명1",
                accountRole = AccountRole.OWNER,
                createdAt = createAt,
                cards = cards,
                categories = categories
            )

            val userInfo = getUserInfo()

            BDDMockito.given(accountBookService.findAccountBookDetail(1L, userInfo.userNo))
                .willReturn(accountBookDetailResponse)


            // When && Then
            mockMvc.perform(
                get("/account-books/{accountBookNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(userInfo))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "가계부 상세 조회 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 가계부상세가 정상 조회됩니다.")
                                .tag(ACCOUNTBOOK_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("accountBookNo").description("가계부 No"),
                                    PayloadDocumentation.fieldWithPath("accountBookName").description("가계부명"),
                                    PayloadDocumentation.fieldWithPath("accountBookDesc").description("가계부 설명"),
                                    PayloadDocumentation.fieldWithPath("accountRole").description("해당 가계부 사용자권한"),
                                    PayloadDocumentation.fieldWithPath("createdAt").description("생성일자"),
                                    PayloadDocumentation.fieldWithPath("cards[].cardNo").description("카드 No"),
                                    PayloadDocumentation.fieldWithPath("cards[].cardName").description("카드명"),
                                    PayloadDocumentation.fieldWithPath("cards[].cardType").description("카드타입"),
                                    PayloadDocumentation.fieldWithPath("cards[].cardDesc").description("카드설명"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryNo")
                                        .description("카테고리 No"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryName")
                                        .description("카테고리명"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryDesc")
                                        .description("카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryIcon")
                                        .description("카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("categories[].accountBookNo")
                                        .description("가계뿌 No"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryNo")
                                        .description("자식 카테고리 No"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryName")
                                        .description("자식 카테고리명"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryDesc")
                                        .description("자식 카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryIcon")
                                        .description("자식 카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].accountBookNo")
                                        .description("가계부 No"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].parentCategoryNo")
                                        .description("부모 카테고리 No"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                ).build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }

        @DisplayName("개인의 가계부 존재 하지 않는경우. 실패 400")
        @Test
        fun accountBooksDetail_fail_throw_accountBookNotFoundException() {
            // Given
            val userInfo = getUserInfo()

            BDDMockito.given(accountBookService.findAccountBookDetail(1L, userInfo.userNo))
                .willThrow(AccountBookNotFoundException())


            // When && Then
            mockMvc.perform(
                get("/account-books/{accountBookNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "aergaeaerg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(userInfo))
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "가계부 상세 조회 실패",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 가계부상세가 조회 실패합니다.")
                                .tag(ACCOUNTBOOK_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("errCode").description("에러코드"),
                                    PayloadDocumentation.fieldWithPath("message").description("에러메세지"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                ).build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(org.apache.http.HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }

    }

    private fun getUserInfo(): CurrentUserInfo {
        return CurrentUserInfo("test", "닉네임", 1L, Role.USER)
    }

    private fun getUser(currentUserInfo: CurrentUserInfo): User {
        return User(
            userNo = currentUserInfo.userNo,
            userId = currentUserInfo.userId,
            nickName = currentUserInfo.nickName,
            role = currentUserInfo.role
        )
    }
}