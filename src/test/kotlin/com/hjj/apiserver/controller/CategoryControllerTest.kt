package com.hjj.apiserver.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.category.CategoryDto
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.request.CategoryRemoveRequest
import com.hjj.apiserver.dto.category.response.CategoryAddResponse
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.CategoryService
import com.hjj.apiserver.utils.ApiDocumentUtil
import org.apache.http.HttpHeaders
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime

@DisplayName("카테고리 관리 API 테스트")
@AutoConfigureRestDocs
@WebMvcTest(CategoryController::class)
@ImportAutoConfiguration(
    exclude = [OAuth2ClientAutoConfiguration::class]
)
class CategoryControllerTest {
    private val CATEGORY_TAG = "카테고리 관리 API"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var categoryService: CategoryService

    @DisplayName("POST /categories API 성공 200")
    @Nested
    inner class Categories_post_success {
        @DisplayName("카테고리가 등록된다.")
        @Test
        fun categoryAddTest_success() {
            //Given
            val categoryAddRequest = CategoryAddRequest(
                accountBookNo = 1L,
                categoryName = "식비",
                categoryDesc = "평소 식비에 사용한 카테고리",
                categoryIcon = "123124.png"
            )

            val categoryAddResponse = CategoryAddResponse(
                accountBookNo = categoryAddRequest.accountBookNo,
                categoryNo = 1L,
                parentCategoryNo = categoryAddRequest.parentCategoryNo,
                categoryName = categoryAddRequest.categoryName,
                categoryDesc = categoryAddRequest.categoryDesc,
                categoryIcon = categoryAddRequest.categoryIcon
            )
            val userInfo = createUserInfo()

            BDDMockito.given(categoryService.addCategory(userInfo.userNo, categoryAddRequest))
                .willReturn(categoryAddResponse)


            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.post("/categories")
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(categoryAddRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카테고리 등록 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("카테고리를 등록합니다.")
                                .tag(CATEGORY_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("accountBookNo").description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categoryNo").description("카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categoryName").description("카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("categoryDesc").description("카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categoryIcon").description("카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("parentCategoryNo").description("상위 카테고리 고유번호"),
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

    @DisplayName("GET /categories API 성공 200")
    @Nested
    inner class Categories_get_success {
        @DisplayName("가계부에 속한 전체 카테고리가 조회된다.")
        @Test
        fun categoriesFindTest_success() {
            //Given
            val categoryFindAllResponse = CategoryFindAllResponse(
                listOf(
                    CategoryDto(
                        accountBookNo = 1L,
                        categoryNo = 1L,
                        categoryName = "식비",
                        categoryDesc = "식비내역카테고리입니다.",
                        categoryIcon = "food.png",
                        childCategories = listOf(
                            CategoryDto.ChildCategory(
                                accountBookNo = 1L,
                                categoryNo = 10L,
                                categoryName = "일식",
                                categoryDesc = "일식내역 카테고리입니다.",
                                categoryIcon = "japan.png",
                                parentCategoryNo = 1L
                            ),
                            CategoryDto.ChildCategory(
                                accountBookNo = 2L,
                                categoryNo = 11L,
                                categoryName = "한식",
                                categoryDesc = "한식내역 카테고리입니다.",
                                categoryIcon = "korea.png",
                                parentCategoryNo = 1L
                            ),
                        ).toMutableList()
                    ),
                    CategoryDto(
                        accountBookNo = 1L,
                        categoryNo = 2L,
                        categoryName = "의료비",
                        categoryDesc = "의료내역 카테고리입니다.",
                        categoryIcon = "hospital.png",
                        childCategories = mutableListOf()
                    )
                ),
                AccountRole.OWNER
            )

            val userInfo = createUserInfo()

            BDDMockito.given(categoryService.findAllCategories(userInfo.userNo, 1L))
                .willReturn(categoryFindAllResponse)

            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
            map.add("accountBookNo", "1")

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/categories")
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
                        "카테고리 전체 목록 조회 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("카테고리를 전체 조회합니다.")
                                .tag(CATEGORY_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("categories")
                                        .description("카테고리 정보"),
                                    PayloadDocumentation.fieldWithPath("categories[].accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryNo")
                                        .description("카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryName")
                                        .description("카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryDesc")
                                        .description("카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categories[].categoryIcon")
                                        .description("카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories")
                                        .description("하위 카테고리정보"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryNo")
                                        .description("하위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryName")
                                        .description("하위 카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryDesc")
                                        .description("하위 카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].categoryIcon")
                                        .description("하위 카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].parentCategoryNo")
                                        .description("소속된 상위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categories[].childCategories[].accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("accountRole")
                                        .description("사용자의 가계부 권한"),
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


    @DisplayName("GET /categories/{categoryNo} API 성공 200")
    @Nested
    inner class Category_get_success {
        @DisplayName("카테고리를 상세 조회된다.")
        @Test
        fun categoryDetailTest_success() {
            //Given
            val categoryFindAllResponse = CategoryDetailResponse(
                accountBookNo = 1L,
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "식비내역카테고리입니다.",
                categoryIcon = "food.png",
                childCategories = listOf(
                    CategoryDetailResponse.ChildCategory(
                        accountBookNo = 1L,
                        categoryNo = 10L,
                        parentCategoryNo = 1L,
                        categoryName = "일식",
                        categoryDesc = "일식내역 카테고리입니다.",
                        categoryIcon = "japan.png",
                        createdAt = LocalDateTime.now(),
                        modifiedAt = LocalDateTime.now()
                    ),
                    CategoryDetailResponse.ChildCategory(
                        accountBookNo = 2L,
                        categoryNo = 11L,
                        parentCategoryNo = 1L,
                        categoryName = "한식",
                        categoryDesc = "한식내역 카테고리입니다.",
                        categoryIcon = "korea.png",
                        createdAt = LocalDateTime.now(),
                        modifiedAt = LocalDateTime.now()
                    ),
                )
            )

            val userInfo = createUserInfo()

            BDDMockito.given(categoryService.findCategory(1L, userInfo.userNo))
                .willReturn(categoryFindAllResponse)


            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.get("/categories/{categoryNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카테고리 상세 조회 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("카테고리를 상세 조회합니다.")
                                .tag(CATEGORY_TAG)
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("parentCategoryNo")
                                        .description("상위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categoryNo")
                                        .description("카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("categoryName")
                                        .description("카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("categoryDesc")
                                        .description("카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("categoryIcon")
                                        .description("카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("childCategories")
                                        .description("하위 카테고리정보"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].categoryNo")
                                        .description("하위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].categoryName")
                                        .description("하위 카테고리 이름"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].categoryDesc")
                                        .description("하위 카테고리 설명"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].categoryIcon")
                                        .description("하위 카테고리 아이콘"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].parentCategoryNo")
                                        .description("소속된 상위 카테고리 고유번호"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].accountBookNo")
                                        .description("가계부 고유번호"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].createdAt")
                                        .description("카테고리 생성일자"),
                                    PayloadDocumentation.fieldWithPath("childCategories[].modifiedAt")
                                        .description("카테고리 수정일자"),
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

    @DisplayName("PATCH /categories API 성공 200")
    @Nested
    inner class Categories_patch_success {
        @DisplayName("카테고리를 수정한다.")
        @Test
        fun categoryModifyTest_success() {
            //Given
            val categoryModifyRequest = CategoryModifyRequest(
                accountBookNo = 1L,
                categoryName = "카테고리명",
                categoryDesc = "설명",
                categoryIcon = "abc.png"
            )

            val userInfo = createUserInfo()


            Mockito.doNothing().`when`(categoryService).modifyCategory(userInfo.userNo, 1L, categoryModifyRequest)

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/categories/{categoryNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(categoryModifyRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카테고리 수정 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("카테고리를 수정합니다.")
                                .tag(CATEGORY_TAG)
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


    @DisplayName("DELETE /categories API 성공 200")
    @Nested
    inner class Categories_delete_success {
        @DisplayName("카테고리를 삭제한다.")
        @Test
        fun categoryRemoveTest_success() {
            //Given
            val categoryRemoveRequest = CategoryRemoveRequest(
                accountBookNo = 1L,
            )

            val userInfo = createUserInfo()


            Mockito.doNothing().`when`(categoryService).deleteCategory(1L, categoryRemoveRequest.accountBookNo, userInfo.userNo)

            //When && Then
            mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/categories/{categoryNo}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + "dXNlcjpzZWNyZXQ=")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(categoryRemoveRequest))
                    .with(
                        SecurityMockMvcRequestPostProcessors.user(userInfo)
                    ).with(
                        SecurityMockMvcRequestPostProcessors.csrf(),
                    )
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(
                    MockMvcRestDocumentationWrapper.document(
                        "카테고리 삭제 성공",
                        ApiDocumentUtil.getDocumentResponse(),
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .description("카테고리를 삭제합니다.")
                                .tag(CATEGORY_TAG)
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

    private fun getUser(currentUserInfo: CurrentUserInfo): User {
        return User(
            userNo = currentUserInfo.userNo,
            userId = currentUserInfo.userId,
            nickName = currentUserInfo.nickName,
            role = currentUserInfo.role
        )
    }
}