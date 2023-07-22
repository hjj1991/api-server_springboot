package com.hjj.apiserver.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseAddResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseFindOfPageResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.PurchaseService
import com.hjj.apiserver.util.CommonUtils
import com.hjj.apiserver.utils.ApiDocumentUtil
import org.apache.http.HttpHeaders
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
import org.springframework.data.domain.SliceImpl
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DisplayName("지출 관리 API 테스트")
@AutoConfigureRestDocs
@WebMvcTest(PurchaseController::class)
@ImportAutoConfiguration(
    exclude = [OAuth2ClientAutoConfiguration::class]
)
class PurchaseControllerTest {
    private val PURCHASE_TAG = "지출 관리 API"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var purchaseService: PurchaseService

    @DisplayName("POST /purchase API 성공 200")
    @Nested
    inner class Purchase_post_success {
        @DisplayName("지출이 등록된다.")
        @Test
        fun purchaseAddTest_success_when_outGoing() {
            //Given
            val purchaseAddRequest = PurchaseAddRequest(
                1L,
                1L,
                null,
                PurchaseType.OUTGOING,
                10000,
                "식비",
                LocalDate.of(2023, 7, 14)
            )

            val purchaseAddResponse = PurchaseAddResponse(
                purchaseNo = 1L,
                accountBookNo = purchaseAddRequest.accountBookNo,
                cardNo = purchaseAddRequest.cardNo,
                categoryNo = purchaseAddRequest.categoryNo,
                purchaseType = PurchaseType.OUTGOING,
                price = purchaseAddRequest.price,
                reason = purchaseAddRequest.reason,
                purchaseDate = purchaseAddRequest.purchaseDate
            )

            val userInfo = createUserInfo()

            BDDMockito.given(purchaseService.addPurchase(userInfo.userNo, purchaseAddRequest))
                .willReturn(purchaseAddResponse)


            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/purchase")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(purchaseAddRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "지출내역 등록 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("지출내역을 등록합니다.")
                                .tag(PURCHASE_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("purchaseNo").description("지출 내역 고유번호"),
                                    PayloadDocumentation.fieldWithPath("accountBookNo").description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categoryNo").description("카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("cardNo").description("카드 고유번호"),
                                    PayloadDocumentation.fieldWithPath("purchaseType").description("지출 유형"),
                                    PayloadDocumentation.fieldWithPath("price").description("금액"),
                                    PayloadDocumentation.fieldWithPath("reason").description("사유"),
                                    PayloadDocumentation.fieldWithPath("purchaseDate").description("지출 또는 수입 일자"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
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

    @DisplayName("GET /purchase API 성공 200")
    @Nested
    inner class Purchase_get_success {
        @DisplayName("가계부에 속한 지출 내역이 조회된다.")
        @Test
        fun purchasesFindTest_success() {
            //Given
            val purchaseFindOfPageRequest = PurchaseFindOfPageRequest(
                accountBookNo = 1L,
                startDate = LocalDate.of(2023, 7, 21),
                endDate = LocalDate.of(2023, 7, 30)
            )

            val userInfo = createUserInfo()

            val purchaseByOutGoing = PurchaseFindOfPageResponse(
                purchaseNo = 1L,
                userNo = userInfo.userNo,
                cardNo = 1L,
                accountBookNo = purchaseFindOfPageRequest.accountBookNo,
                purchaseType = PurchaseType.OUTGOING,
                price = 10000,
                reason = "호호식당 음식값",
                purchaseDate = LocalDate.of(2023, 7, 22),
                categoryInfo = PurchaseFindOfPageResponse.PurchaseCategoryInfo(
                    categoryNo = 1L,
                    categoryName = "식비",
                    categoryIcon = "124.png",
                    categoryDesc = "평소 식사비"
                )
            )

            val purchaseByInCome = PurchaseFindOfPageResponse(
                purchaseNo = 2L,
                userNo = userInfo.userNo,
                accountBookNo = purchaseFindOfPageRequest.accountBookNo,
                purchaseType = PurchaseType.INCOME,
                price = 1000000,
                reason = "급여",
                purchaseDate = LocalDate.of(2023, 7, 23),
            )

            val pageRequest = purchaseFindOfPageRequest.getPageRequest()

            val purchaseFindOfPageResponse = SliceImpl(
                CommonUtils.getSlicePageResult(listOf(purchaseByOutGoing, purchaseByInCome), pageRequest.pageSize),
                pageRequest,
                false
            )

            BDDMockito.given(
                purchaseService.findPurchasesOfPage(
                    purchaseFindOfPageRequest,
                    purchaseFindOfPageRequest.getPageRequest()
                )
            )
                .willReturn(purchaseFindOfPageResponse)

            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
            map.add("accountBookNo", purchaseFindOfPageRequest.accountBookNo.toString())
            map.add("startDate", purchaseFindOfPageRequest.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            map.add("endDate", purchaseFindOfPageRequest.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            map.add("size", purchaseFindOfPageRequest.size.toString())
            map.add("page", purchaseFindOfPageRequest.page.toString())

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/purchase")
                    .queryParams(map)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "지출 내역목록 페이징 조회 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("지출 내역목록 페이징 조회합니다.")
                                .tag(PURCHASE_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("content")
                                        .description("지출 목록"),
                                    PayloadDocumentation.fieldWithPath("content[].purchaseNo")
                                        .description("지출 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].userNo")
                                        .description("사용자 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].cardNo").optional()
                                        .description("카드 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].purchaseType")
                                        .description("지출 형태"),
                                    PayloadDocumentation.fieldWithPath("content[].price")
                                        .description("금액"),
                                    PayloadDocumentation.fieldWithPath("content[].reason")
                                        .description("사유"),
                                    PayloadDocumentation.fieldWithPath("content[].purchaseDate")
                                        .description("지출 일자"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo").optional()
                                        .description("하위 카테고리 정보"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.categoryNo")
                                        .description("하위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.categoryName")
                                        .description("하위 카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.categoryDesc")
                                        .description("하위 카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.categoryIcon")
                                        .description("하위 카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.parentCategoryNo")
                                        .description("소속된 상위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("content[].categoryInfo.parentCategoryName")
                                        .description("소속된 상위 카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("pageable")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.sort")
                                        .description("페이징 정렬 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.sort.empty")
                                        .description("페이징 정렬 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.sort.sorted")
                                        .description("페이징 정렬 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.sort.unsorted")
                                        .description("페이징 정렬 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.offset")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.pageSize")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.pageNumber")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.unpaged")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("pageable.paged")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("size")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("number")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("sort")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("sort.empty")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("sort.sorted")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("sort.unsorted")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("first")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("last")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("numberOfElements")
                                        .description("페이징 관련 정보"),
                                    PayloadDocumentation.fieldWithPath("empty")
                                        .description("페이징 관련 정보"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
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

    @DisplayName("DELETE /purchase API 성공 200")
    @Nested
    inner class Purchase_delete_success {
        @DisplayName("가계부에 속한 지출 내역을 삭제한다.")
        @Test
        fun purchaseRemoveTest_success() {
            //Given

            val userInfo = createUserInfo()

            BDDMockito.doNothing().`when`(purchaseService).removePurchase(userInfo.userNo, 1L)

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/purchase/{purchaseNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "지출 내역삭제 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("지출 내역삭제 성공합니다.")
                                .tag(PURCHASE_TAG)
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
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

    @DisplayName("GET /purchase/{purchaseNo} API 성공 200")
    @Nested
    inner class PurchaseDetail_get_success {
        @DisplayName("가계부에 속한 지출 내역 상세조회가 된다.")
        @Test
        fun purchasesFindTest_success() {
            //Given

            val userInfo = createUserInfo()

            val purchaseDetailResponse = PurchaseDetailResponse(
                purchaseNo = 1L,
                accountBookNo = 1L,
                cardNo = 2L,
                categoryNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 10000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 5, 1)
            )

            BDDMockito.given(purchaseService.findPurchase(userInfo.userNo, 1L))
                .willReturn(purchaseDetailResponse)


            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/purchase/{purchaseNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "지출 내역상세 조회 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("지출 내역을 상세 조회합니다.")
                                .tag(PURCHASE_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("purchaseNo")
                                        .description("지출 고유번호"),
                                    PayloadDocumentation.fieldWithPath("cardNo").optional()
                                        .description("카드 고유번호"),
                                    PayloadDocumentation.fieldWithPath("accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("purchaseType")
                                        .description("지출 형태"),
                                    PayloadDocumentation.fieldWithPath("price")
                                        .description("금액"),
                                    PayloadDocumentation.fieldWithPath("reason")
                                        .description("사유"),
                                    PayloadDocumentation.fieldWithPath("purchaseDate")
                                        .description("지출 일자"),
                                    PayloadDocumentation.fieldWithPath("categoryNo")
                                        .description("하위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("parentCategoryNo")
                                        .description("소속된 상위 카테고리 고유번호"),
                                )
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
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

    @DisplayName("PATCH /purchase API 성공 200")
    @Nested
    inner class Purchase_patch_success {
        @DisplayName("가계부에 속한 지출 내역을 수정한다.")
        @Test
        fun purchaseModifyTest_success() {
            //Given

            val userInfo = createUserInfo()

            val purchaseModifyRequest = PurchaseModifyRequest(
                accountBookNo = 1L,
                cardNo = 1L, categoryNo = 1L, purchaseType = PurchaseType.OUTGOING,
                price = 5000000, reason = "변경이유", purchaseDate = LocalDate.of(2023, 7, 6)
            )

            BDDMockito.doNothing().`when`(purchaseService).modifyPurchase(userInfo.userNo, 1L, purchaseModifyRequest)

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/purchase/{purchaseNo}", 1L)
                    .content(objectMapper.writeValueAsBytes(purchaseModifyRequest))
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "지출 내역수정 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("지출 내역수정 성공합니다.")
                                .tag(PURCHASE_TAG)
                                .requestHeaders(
                                    ResourceDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description(
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

    private fun createUserInfo(): CurrentUserInfo {
        return CurrentUserInfo("test", "닉네임", 1L, Role.USER)
    }
}