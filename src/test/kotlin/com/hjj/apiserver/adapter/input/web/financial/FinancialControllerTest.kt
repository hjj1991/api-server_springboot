package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.application.port.input.financial.GetFinancialUseCase
import com.hjj.apiserver.common.ApiProblemFactory
import com.hjj.apiserver.common.ExceptionControllerAdvice
import com.hjj.apiserver.common.exception.financial.FinancialProductNotFoundException
import com.hjj.apiserver.config.ErrorResponseProperties
import com.hjj.apiserver.domain.financial.FinancialCompany
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProduct
import com.hjj.apiserver.domain.financial.FinancialProductOption
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.InterestRateType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ReserveType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal

class FinancialControllerTest {
    private lateinit var mockMvc: MockMvc

    private val getFinancialUseCase: GetFinancialUseCase = Mockito.mock(GetFinancialUseCase::class.java)

    @BeforeEach
    fun setUp() {
        Mockito.reset(getFinancialUseCase)

        val errorResponseProperties = ErrorResponseProperties().apply {
            problemTypeBaseUri = "https://api.test.local/problems"
        }

        mockMvc =
            MockMvcBuilders.standaloneSetup(
                FinancialController(
                    getFinancialUseCase = getFinancialUseCase,
                ),
            )
                .setControllerAdvice(ExceptionControllerAdvice(ApiProblemFactory(errorResponseProperties)))
                .build()
    }

    @Test
    fun `금융상품 단건 조회 성공`() {
        Mockito.`when`(getFinancialUseCase.getFinancialProduct(1L)).thenReturn(sampleProduct())

        mockMvc.perform(
            get("/financial-products/{financialProductId}", 1L)
                .header("API-Version", "1.0"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.financialProductId").value(1))
            .andExpect(jsonPath("$.financialProductName").value("테스트 예금"))
            .andExpect(jsonPath("$.financialCompany.companyName").value("테스트은행"))
            .andExpect(jsonPath("$.financialProductOptions[0].interestRateType").value("단리"))
    }

    @Test
    fun `금융상품 단건 조회 실패시 ProblemDetail 반환`() {
        Mockito.`when`(getFinancialUseCase.getFinancialProduct(999L)).thenThrow(FinancialProductNotFoundException())

        mockMvc.perform(
            get("/financial-products/{financialProductId}", 999L)
                .header("API-Version", "1.0"),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("ERR_CODE0014"))
            .andExpect(jsonPath("$.status").value(404))
    }

    private fun sampleProduct(): FinancialProduct {
        val company =
            FinancialCompany(
                financialCompanyId = 10L,
                financialCompanyCode = "001",
                dclsMonth = "202601",
                companyName = "테스트은행",
                financialGroupType = FinancialGroupType.BANK,
            )

        val option =
            FinancialProductOption(
                financialProductOptionId = 100L,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = ReserveType.FIXED,
                depositPeriodMonths = "12",
                baseInterestRate = BigDecimal("3.10"),
                maximumInterestRate = BigDecimal("3.50"),
                financialProduct = null,
            )

        return FinancialProduct(
            financialProductId = 1L,
            financialProductCode = "PRD-001",
            financialProductName = "테스트 예금",
            joinWay = "영업점",
            postMaturityInterestRate = "만기 후 1%",
            specialCondition = "급여이체 우대",
            joinRestriction = JoinRestriction.NO_RESTRICTION,
            financialProductType = FinancialProductType.SAVINGS,
            joinMember = "개인",
            additionalNotes = "중도해지시 불이익",
            maxLimit = 100000000,
            dclsMonth = "202601",
            dclsStartDay = "2026-01-01",
            dclsEndDay = "2026-01-31",
            financialCompany = company,
            financialProductOptions = mutableListOf(option),
        )
    }
}
