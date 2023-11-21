package com.hjj.apiserver.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.card.response.CardModifyResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.impl.CardService
import com.hjj.apiserver.utils.ApiDocumentUtil
import org.apache.http.HttpHeaders
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.*
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@DisplayName("카드 관리 API 테스트")
@AutoConfigureRestDocs
@WebMvcTest(CardController::class)
@ImportAutoConfiguration(
    exclude = [OAuth2ClientAutoConfiguration::class],
)
class CardControllerTest {

    private val CARD_TAG = "카드 관리 API"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var cardService: CardService


    @DisplayName("GET /cards API 성공 200")
    @Nested
    inner class Cards_get_success {
        @DisplayName("개인의 카드 전체 목록을 가져온다.")
        @Test
        fun cardsFindTest_success() {
            val cardFindAllResponses = listOf(
                CardFindAllResponse(
                    cardNo = 1L,
                    cardName = "카드이름",
                    cardType = CardType.CREDIT_CARD,
                    cardDesc = "카드설명"
                )
            )
            //Given
            given(cardService.findCards(anyLong()))
                .willReturn(cardFindAllResponses)
            val userInfo = getUserInfo()

            //When && Then

            mockMvc.perform(
                get("/cards")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    )
            )
                .andExpect(status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카드 조회 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 카드 목록을 조회합니다.")
                                .tag(CARD_TAG)
                                .responseFields(
                                    fieldWithPath("[].cardNo").description("카드번호"),
                                    fieldWithPath("[].cardName").description("카드이름"),
                                    fieldWithPath("[].cardType").description("카드타입"),
                                    fieldWithPath("[].cardDesc").description("카드설명")
                                )
                                .requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                )
                                .build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )

        }
    }


    @DisplayName("POST /cards API 성공 201")
    @Nested
    inner class Cards_post_success {
        @DisplayName("개인의 카드를 등록한다.")
        @Test
        fun cardsAddTest_success() {

            //Given
            val cardAddRequest = CardAddRequest(
                cardName = "카카오카드",
                cardType = CardType.CREDIT_CARD,
                cardDesc = "카카오카드 설명"
            )

            val cardAddResponse = CardAddResponse(
                cardNo = 1,
                cardName = cardAddRequest.cardName,
                cardType = cardAddRequest.cardType,
                cardDesc = cardAddRequest.cardDesc
            )

            val userInfo = getUserInfo()


            given(cardService.addCard(userInfo.userNo, cardAddRequest))
                .willReturn(cardAddResponse)


            //When && Then

            mockMvc.perform(
                post("/cards")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(cardAddRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo),
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(status().isCreated)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카드 등록 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 카드가 정상 등록됩니다.")
                                .tag(CARD_TAG)
                                .responseFields(
                                    fieldWithPath("cardNo").description("카드번호"),
                                    fieldWithPath("cardName").description("카드이름"),
                                    fieldWithPath("cardType").description("카드타입"),
                                    fieldWithPath("cardDesc").description("카드설명")
                                )
                                .requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                )
                                .build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }
    }


    @DisplayName("GET /cards/{cardNo} API 성공 200")
    @Nested
    inner class Cards_detail_get_success {
        @DisplayName("개인의 카드를 상세조회한다.")
        @Test
        fun cardsDetail_Test_success() {

            //Given
            val userInfo = getUserInfo()
            val cardNo = 1L

            val cardFindResponse = CardFindResponse(
                cardNo = cardNo,
                cardName = "현대카드",
                cardType = CardType.CREDIT_CARD,
                cardDesc = "카드 설명입니다."
            )


            given(cardService.findCardDetail(userInfo.userNo, cardNo))
                .willReturn(cardFindResponse)


            //When && Then

            mockMvc.perform(
                get("/cards/{cardNo}", cardNo)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo),
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카드 조회 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 카드가 정상 조회됩니다.")
                                .tag(CARD_TAG)
                                .responseFields(
                                    fieldWithPath("cardNo").description("카드번호"),
                                    fieldWithPath("cardName").description("카드이름"),
                                    fieldWithPath("cardType").description("카드타입"),
                                    fieldWithPath("cardDesc").description("카드설명")
                                )
                                .requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                )
                                .build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }
    }


    @DisplayName("DELETE /cards/{cardNo} API 성공 200")
    @Nested
    inner class Cards_delete_success {
        @DisplayName("개인의 카드를 삭제한다.")
        @Test
        fun cards_delete_Test_success() {

            //Given
            val userInfo = getUserInfo()
            val cardNo = 1L

            //When && Then

            mockMvc.perform(
                delete("/cards/{cardNo}", cardNo)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo),
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(status().isNoContent)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카드 삭제 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 카드가 정상 삭제됩니다.")
                                .tag(CARD_TAG)
                                .requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                )
                                .build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }
    }

    @DisplayName("PUT /cards/{cardNo} API 성공 200")
    @Nested
    inner class Cards_put_success {
        @DisplayName("개인의 카드를 수정한다.")
        @Test
        fun cardsPutTest_success() {

            //Given
            val cardModifyRequest = CardModifyRequest(
                cardName = "카카오카드",
                cardType = CardType.CREDIT_CARD,
                cardDesc = "카카오카드 설명"
            )

            val cardModifyResponse = CardModifyResponse(
                cardNo = 1,
                cardName = cardModifyRequest.cardName,
                cardType = cardModifyRequest.cardType,
                cardDesc = cardModifyRequest.cardDesc
            )

            val userInfo = getUserInfo()


            given(cardService.modifyCard(userInfo.userNo, 1L, cardModifyRequest))
                .willReturn(cardModifyResponse)


            //When && Then

            mockMvc.perform(
                put("/cards/{cardNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(cardModifyRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo),
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카드 수정 성공",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        resource(
                            ResourceSnippetParameters.builder()
                                .description("사용자의 카드가 정상 수정됩니다.")
                                .tag(CARD_TAG)
                                .responseFields(
                                    fieldWithPath("cardNo").description("카드번호"),
                                    fieldWithPath("cardName").description("카드이름"),
                                    fieldWithPath("cardType").description("카드타입"),
                                    fieldWithPath("cardDesc").description("카드설명")
                                )
                                .requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description(
                                        JwtTokenProvider.BEARER_PREFIX + "JWT토큰값"
                                    )
                                )
                                .build()
                        ),
                        HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description(JwtTokenProvider.BEARER_PREFIX + "JWT토큰값")
                        ),
                    )
                )
        }
    }

    private fun getUserInfo(): CurrentUserInfo {
        return CurrentUserInfo("test", "닉네임", 1L, Role.USER)
    }
}