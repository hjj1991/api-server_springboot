package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialCompanyRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductRepository
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.InterestRateType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import com.hjj.apiserver.domain.financial.ReserveType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.sql.DataSource
import org.springframework.core.io.ClassPathResource

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=none",
    ],
)
class FinancialProductSearchIntegrationTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var financialCompanyRepository: FinancialCompanyRepository

    @Autowired
    private lateinit var financialProductRepository: FinancialProductRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        ResourceDatabasePopulator(ClassPathResource("sql/financial-batch-finance-schema.sql")).execute(dataSource)
        jdbcTemplate.execute("TRUNCATE TABLE financial_product_option, financial_product, financial_company RESTART IDENTITY CASCADE")
        seedProducts()
    }

    @Test
    fun `기간 필터가 postgres 쿼리에서 실제로 적용된다`() {
        mockMvc.perform(
            get("/financial-products")
                .header("API-Version", "1.0")
                .param("depositPeriodMonths", "12")
                .param("financialProductName", "테스트 상품 12")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].financialProductName").value("테스트 상품 12"))
            .andExpect(jsonPath("$.content[0].dclsStartDay").value("2026-03-01"))
            .andExpect(jsonPath("$.content[0].financialProductOptions[0].depositPeriodMonths").value("12"))
            .andExpect(jsonPath("$.content[0].financialProductOptions[0].baseInterestRate").value(3.12345))
            .andExpect(jsonPath("$.content[0].financialProductOptions[0].maximumInterestRate").value(4.10000))
    }

    @Test
    fun `financial submit day 가 null 이어도 목록 조회가 실패하지 않는다`() {
        mockMvc.perform(
            get("/financial-products")
                .header("API-Version", "1.0")
                .param("financialProductName", "테스트 상품 null")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].financialProductName").value("테스트 상품 null"))
    }

    @Test
    fun `financial submit day 가 null 이어도 단건 조회가 실패하지 않는다`() {
        val productId =
            financialProductRepository.findAll()
                .first { it.financialProductName == "테스트 상품 null" }
                .financialProductId

        mockMvc.perform(
            get("/financial-products/{financialProductId}", productId)
                .header("API-Version", "1.0"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.financialProductName").value("테스트 상품 null"))
            .andExpect(jsonPath("$.financialCompany.companyName").value("테스트은행"))
    }

    @Test
    fun `최고 금리순 정렬은 상품별 최고 금리 기준으로 적용된다`() {
        mockMvc.perform(
            get("/financial-products")
                .header("API-Version", "1.0")
                .param("sort", "maximumInterestRate,desc")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].financialProductName").value("테스트 상품 12"))
            .andExpect(jsonPath("$.content[1].financialProductName").value("테스트 상품 24"))
            .andExpect(jsonPath("$.content[2].financialProductName").value("테스트 상품 null"))
    }

    @Test
    fun `통합 검색 q 는 상품명과 우대 조건을 함께 검색한다`() {
        mockMvc.perform(
            get("/financial-products")
                .header("API-Version", "1.0")
                .param("q", "급여")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].financialProductName").value("테스트 상품 24"))
    }

    private fun seedProducts() {
        val company =
            financialCompanyRepository.saveAndFlush(
                FinancialCompanyEntity(
                    financialCompanyCode = "0010001",
                    dclsMonth = "202603",
                    companyName = "테스트은행",
                    financialGroupType = FinancialGroupType.BANK,
                ),
            )

        financialProductRepository.saveAndFlush(
            createProduct(
                company = company,
                code = "PRD-12",
                depositPeriodMonths = 12,
                maximumInterestRate = BigDecimal("4.10000"),
                additionalOptionRates =
                    listOf(
                        OptionRate(periodMonths = 24, baseRate = BigDecimal("3.00000"), maximumRate = BigDecimal("3.40000")),
                    ),
            ),
        )
        financialProductRepository.saveAndFlush(
            createProduct(
                company = company,
                code = "PRD-24",
                depositPeriodMonths = 24,
                maximumInterestRate = BigDecimal("3.80000"),
                specialCondition = "급여 이체 시 우대",
            ),
        )
        financialProductRepository.saveAndFlush(
            createProduct(
                company = company,
                code = "PRD-NULL",
                depositPeriodMonths = 12,
                financialProductName = "테스트 상품 null",
                financialSubmitDay = null,
                maximumInterestRate = BigDecimal("3.20000"),
            ),
        )
    }

    private fun createProduct(
        company: FinancialCompanyEntity,
        code: String,
        depositPeriodMonths: Int,
        financialProductName: String = "테스트 상품 $depositPeriodMonths",
        financialSubmitDay: OffsetDateTime? = OffsetDateTime.parse("2026-03-01T00:00:00Z"),
        baseInterestRate: BigDecimal = BigDecimal("3.12345"),
        maximumInterestRate: BigDecimal = BigDecimal("3.56789"),
        specialCondition: String = "우대조건",
        additionalOptionRates: List<OptionRate> = emptyList(),
    ): FinancialProductEntity {
        val product =
            FinancialProductEntity(
                financialProductCode = code,
                financialProductName = financialProductName,
                joinWay = "영업점",
                postMaturityInterestRate = "만기 후 1%",
                specialCondition = specialCondition,
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                joinMember = "개인",
                additionalNotes = "비고",
                maxLimit = 1000000,
                dclsMonth = "202603",
                dclsStartDay = LocalDate.parse("2026-03-01"),
                dclsEndDay = LocalDate.parse("2026-03-31"),
                financialSubmitDay = financialSubmitDay,
                financialCompanyEntity = company,
                status = ProductStatus.ACTIVE,
                lastSeenAt = OffsetDateTime.parse("2026-03-21T00:00:00Z"),
                productContentHash = "a".repeat(64),
            )
        val option =
            FinancialProductOptionEntity(
                interestRateType = InterestRateType.SIMPLE,
                reserveType = ReserveType.FIXED,
                depositPeriodMonths = depositPeriodMonths,
                baseInterestRate = baseInterestRate,
                maximumInterestRate = maximumInterestRate,
                financialProductEntity = product,
            )
        product.financialProductOptionEntities.add(option)

        additionalOptionRates.forEach { optionRate ->
            product.financialProductOptionEntities.add(
                FinancialProductOptionEntity(
                    interestRateType = InterestRateType.SIMPLE,
                    reserveType = ReserveType.FLEXIBLE,
                    depositPeriodMonths = optionRate.periodMonths,
                    baseInterestRate = optionRate.baseRate,
                    maximumInterestRate = optionRate.maximumRate,
                    financialProductEntity = product,
                ),
            )
        }

        return product
    }

    private data class OptionRate(
        val periodMonths: Int,
        val baseRate: BigDecimal,
        val maximumRate: BigDecimal,
    )

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer<Nothing>("pgvector/pgvector:pg16")
                .apply {
                    withDatabaseName("api_server_test")
                    withUsername("postgres")
                    withPassword("postgres")
                }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName)
        }
    }
}
