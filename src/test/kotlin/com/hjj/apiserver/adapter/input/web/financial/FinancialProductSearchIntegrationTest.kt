package com.hjj.apiserver.adapter.input.web.financial

import com.github.dockerjava.api.command.InspectContainerResponse
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
import java.time.OffsetDateTime

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=create-drop",
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

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        financialProductRepository.deleteAll()
        financialCompanyRepository.deleteAll()
        seedProducts()
    }

    @Test
    fun `기간 필터가 postgres 쿼리에서 실제로 적용된다`() {
        mockMvc.perform(
            get("/financial-products")
                .header("API-Version", "1.0")
                .param("depositPeriodMonths", "12")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].financialProductName").value("테스트 상품 12"))
            .andExpect(jsonPath("$.content[0].financialProductOptions[0].depositPeriodMonths").value("12"))
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

        financialProductRepository.saveAndFlush(createProduct(company, "PRD-12", 12))
        financialProductRepository.saveAndFlush(createProduct(company, "PRD-24", 24))
    }

    private fun createProduct(
        company: FinancialCompanyEntity,
        code: String,
        depositPeriodMonths: Int,
    ): FinancialProductEntity {
        val product =
            FinancialProductEntity(
                financialProductCode = code,
                financialProductName = "테스트 상품 $depositPeriodMonths",
                joinWay = "영업점",
                postMaturityInterestRate = "만기 후 1%",
                specialCondition = "우대조건",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                joinMember = "개인",
                additionalNotes = "비고",
                maxLimit = 1000000,
                dclsMonth = "202603",
                dclsStartDay = "2026-03-01",
                dclsEndDay = "2026-03-31",
                financialSubmitDay = "2026-03-01T00:00:00Z",
                financialCompanyEntity = company,
                status = ProductStatus.ACTIVE,
                lastSeenAt = OffsetDateTime.parse("2026-03-21T00:00:00Z"),
            )
        val option =
            FinancialProductOptionEntity(
                interestRateType = InterestRateType.SIMPLE,
                reserveType = ReserveType.FIXED,
                depositPeriodMonths = depositPeriodMonths,
                baseInterestRate = BigDecimal("3.10"),
                maximumInterestRate = BigDecimal("3.50"),
                financialProductEntity = product,
            )
        product.financialProductOptionEntities.add(option)
        return product
    }

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            object : PostgreSQLContainer<Nothing>("pgvector/pgvector:pg16") {
                override fun containerIsStarted(containerInfo: InspectContainerResponse) {
                    super.containerIsStarted(containerInfo)
                    val result =
                        execInContainer(
                            "psql",
                            "-U",
                            username,
                            "-d",
                            databaseName,
                            "-c",
                            "CREATE EXTENSION IF NOT EXISTS vector",
                        )
                    check(result.exitCode == 0) {
                        "Failed to create vector extension: ${result.stderr}"
                    }
                }
            }
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
