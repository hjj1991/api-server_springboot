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
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.sql.DataSource

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=none",
    ],
)
class FinancialHistoryControllerIntegrationTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var financialCompanyRepository: FinancialCompanyRepository

    @Autowired
    private lateinit var financialProductRepository: FinancialProductRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var mockMvc: MockMvc

    private var financialProductId: Long = 0L

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        ResourceDatabasePopulator(ClassPathResource("sql/financial-batch-finance-schema.sql")).execute(dataSource)
        jdbcTemplate.execute(
            "TRUNCATE TABLE financial_product_rate_history, financial_product_history, financial_product_option, financial_product, financial_company RESTART IDENTITY CASCADE",
        )
        seedHistoryData()
    }

    @Test
    fun `상품 이력 조회는 cursor 없이도 정상 응답한다`() {
        mockMvc.perform(
            get("/financial-products/{financialProductId}/histories", financialProductId)
                .header("API-Version", "1.0")
                .param("limit", "5"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(2))
            .andExpect(jsonPath("$.items[0].observedAt").value("2026-03-21T11:00:00Z"))
            .andExpect(jsonPath("$.items[0].status").value("ACTIVE"))
            .andExpect(jsonPath("$.items[0].payload").value(containsString("인터넷,스마트폰")))
            .andExpect(jsonPath("$.items[0].payload").value(containsString("급여 이체 시 우대")))
            .andExpect(jsonPath("$.nextCursor").value(nullValue()))
    }

    @Test
    fun `상품 금리 이력 조회는 cursor 없이도 정상 응답한다`() {
        mockMvc.perform(
            get("/financial-products/{financialProductId}/rate-histories", financialProductId)
                .header("API-Version", "1.0")
                .param("limit", "5"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].observedAt").value("2026-03-21T11:00:00Z"))
            .andExpect(jsonPath("$[0].interestRateType").value("SIMPLE"))
            .andExpect(jsonPath("$[0].reserveType").value("FLEXIBLE"))
            .andExpect(jsonPath("$[0].depositPeriodMonths").value(12))
            .andExpect(jsonPath("$[0].maximumInterestRate").value(3.8))
    }

    private fun seedHistoryData() {
        val company =
            financialCompanyRepository.saveAndFlush(
                FinancialCompanyEntity(
                    financialCompanyCode = "0010001",
                    dclsMonth = "202603",
                    companyName = "테스트은행",
                    financialGroupType = FinancialGroupType.BANK,
                ),
            )

        val product =
            financialProductRepository.saveAndFlush(
                createProduct(company),
            )

        val optionId = product.financialProductOptionEntities.first().financialProductOptionId
        financialProductId = product.financialProductId

        jdbcTemplate.update(
            """
            INSERT INTO financial_product_history (
                observed_at,
                financial_product_id,
                financial_company_id,
                financial_product_code,
                financial_product_type,
                status,
                product_content_hash,
                payload
            ) VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
            """.trimIndent(),
            OffsetDateTime.parse("2026-03-21T11:00:00Z"),
            product.financialProductId,
            company.financialCompanyId,
            product.financialProductCode,
            product.financialProductType.name,
            ProductStatus.ACTIVE.name,
            "hash-b",
            """{"joinWay":"인터넷,스마트폰","joinMember":"실명의 개인","spclCnd":"급여 이체 시 우대"}""",
        )
        jdbcTemplate.update(
            """
            INSERT INTO financial_product_history (
                observed_at,
                financial_product_id,
                financial_company_id,
                financial_product_code,
                financial_product_type,
                status,
                product_content_hash,
                payload
            ) VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
            """.trimIndent(),
            OffsetDateTime.parse("2026-03-20T11:00:00Z"),
            product.financialProductId,
            company.financialCompanyId,
            product.financialProductCode,
            product.financialProductType.name,
            ProductStatus.ACTIVE.name,
            "hash-a",
            """{"joinWay":"영업점","joinMember":"실명의 개인","spclCnd":"우대 조건 없음"}""",
        )

        jdbcTemplate.update(
            """
            INSERT INTO financial_product_rate_history (
                observed_at,
                financial_product_id,
                financial_product_option_id,
                interest_rate_type,
                reserve_type,
                deposit_period_months,
                base_interest_rate,
                maximum_interest_rate,
                payload
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
            """.trimIndent(),
            OffsetDateTime.parse("2026-03-21T11:00:00Z"),
            product.financialProductId,
            optionId,
            InterestRateType.SIMPLE.name,
            ReserveType.FLEXIBLE.name,
            12,
            BigDecimal("3.10000"),
            BigDecimal("3.80000"),
            """{"reserveType":"FLEXIBLE","depositPeriodMonths":12}""",
        )
        jdbcTemplate.update(
            """
            INSERT INTO financial_product_rate_history (
                observed_at,
                financial_product_id,
                financial_product_option_id,
                interest_rate_type,
                reserve_type,
                deposit_period_months,
                base_interest_rate,
                maximum_interest_rate,
                payload
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
            """.trimIndent(),
            OffsetDateTime.parse("2026-03-20T11:00:00Z"),
            product.financialProductId,
            optionId,
            InterestRateType.SIMPLE.name,
            ReserveType.FLEXIBLE.name,
            12,
            BigDecimal("2.90000"),
            BigDecimal("3.50000"),
            """{"reserveType":"FLEXIBLE","depositPeriodMonths":12}""",
        )
    }

    private fun createProduct(company: FinancialCompanyEntity): FinancialProductEntity {
        val product =
            FinancialProductEntity(
                financialProductCode = "TEST-1",
                financialProductName = "테스트 적금",
                joinWay = "인터넷,스마트폰",
                postMaturityInterestRate = "만기 후 1.0%",
                specialCondition = "급여 이체 시 우대",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.INSTALLMENT_SAVINGS,
                joinMember = "실명의 개인",
                additionalNotes = "테스트 상품입니다.",
                maxLimit = 1000000,
                dclsMonth = "202603",
                dclsStartDay = LocalDate.parse("2026-03-01"),
                dclsEndDay = LocalDate.parse("2026-03-31"),
                financialSubmitDay = OffsetDateTime.parse("2026-03-01T00:00:00Z"),
                financialCompanyEntity = company,
                status = ProductStatus.ACTIVE,
                lastSeenAt = OffsetDateTime.parse("2026-03-21T00:00:00Z"),
                productContentHash = "a".repeat(64),
            )
        val option =
            FinancialProductOptionEntity(
                interestRateType = InterestRateType.SIMPLE,
                reserveType = ReserveType.FLEXIBLE,
                depositPeriodMonths = 12,
                baseInterestRate = BigDecimal("3.10000"),
                maximumInterestRate = BigDecimal("3.80000"),
                financialProductEntity = product,
            )
        product.financialProductOptionEntities.add(option)
        return product
    }

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
