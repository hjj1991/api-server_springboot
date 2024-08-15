package com.hjj.apiserver.persistence

import com.hjj.apiserver.adapter.out.persistence.financial.FinancialProductPersistenceAdapter
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialCompanyMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductMapper
import com.hjj.apiserver.adapter.out.persistence.financial.converter.FinancialProductOptionMapper
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialCompanyEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductEntity
import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialCompanyRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductCustomRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductOptionRepository
import com.hjj.apiserver.adapter.out.persistence.financial.repository.FinancialProductRepository
import com.hjj.apiserver.application.port.out.financial.GetFinancialProductPort
import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMySqlDBContainer
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.InterestRateType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

@DataJpaTest
@Import(
    TestMySqlDBContainer::class,
    DataSourceConfiguration::class,
    TestConfiguration::class,
    FinancialProductPersistenceAdapter::class,
    FinancialCompanyMapper::class,
    FinancialProductMapper::class,
    FinancialProductOptionMapper::class,
    FinancialProductCustomRepository::class,
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FinancialProductPersistenceAdapterTest {
    @Autowired
    private lateinit var getFinancialProductPort: GetFinancialProductPort

    @Autowired
    private lateinit var financialCompanyRepository: FinancialCompanyRepository

    @Autowired
    private lateinit var financialProductRepository: FinancialProductRepository

    @Autowired
    private lateinit var financialProductOptionRepository: FinancialProductOptionRepository

    @Test
    fun findFinancialProductsWithPaginationInfo_success() {
        // Given
        val financialCompanyEntity =
            FinancialCompanyEntity(
                financialCompanyCode = "0010001",
                companyName = "우리은행",
                financialGroupType = FinancialGroupType.BANK,
                dclsChrgMan = "영업맨",
                hompUrl = "https://www.woori.co.kr",
                calTel = "123-4567-8901",
                dclsMonth = "202209",
            )
        financialCompanyRepository.save(financialCompanyEntity)
        val financialProductEntity =
            FinancialProductEntity(
                financialProductCode = "WR0001B",
                financialProductName = "첫적금",
                joinWay = "일반",
                postMaturityInterestRate = "1년후 0.2",
                specialCondition = "우대방법",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                joinMember = "A123456",
                additionalNotes = "적금 3개월",
                financialCompanyEntity = financialCompanyEntity,
                dclsMonth = "202209",
                dclsStartDay = "20220901",
                dclsEndDay = "20220930",
                financialSubmitDay = "20220910",
            )
        financialProductRepository.save(financialProductEntity)
        val financialProductOptionEntity1 =
            FinancialProductOptionEntity(
                financialProductEntity = financialProductEntity,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = null,
                depositPeriodMonths = "3",
                baseInterestRate = BigDecimal.valueOf(3.00),
                maximumInterestRate = BigDecimal.valueOf(3.4),
            )
        financialProductOptionRepository.save(financialProductOptionEntity1)

        // When
        val financialProducts =
            getFinancialProductPort.findFinancialProductsWithPaginationInfo(
                financialGroupType = FinancialGroupType.BANK,
                companyName = "우리은행",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                financialProductName = "첫적금",
                depositPeriodMonths = "3",
                pageable = PageRequest.of(0, 10),
            )
        // Then
        assertThat(financialProducts.first).hasSize(1)
        assertThat(financialProducts.second).isFalse()

        val financialProduct = financialProducts.first.first()
        assertThat(financialProduct.financialProductCode).isEqualTo("WR0001B")
        assertThat(financialProduct.financialProductName).isEqualTo("첫적금")
        assertThat(financialProduct.joinWay).isEqualTo("일반")
        assertThat(financialProduct.postMaturityInterestRate).isEqualTo("1년후 0.2")
        assertThat(financialProduct.specialCondition).isEqualTo("우대방법")
        assertThat(financialProduct.joinRestriction).isEqualTo(JoinRestriction.NO_RESTRICTION)
        assertThat(financialProduct.financialProductType).isEqualTo(FinancialProductType.SAVINGS)
        assertThat(financialProduct.joinMember).isEqualTo("A123456")
        assertThat(financialProduct.additionalNotes).isEqualTo("적금 3개월")
        assertThat(financialProduct.maxLimit).isEqualTo(null)
        assertThat(financialProduct.dclsMonth).isEqualTo("202209")
        assertThat(financialProduct.dclsStartDay).isEqualTo("20220901")
        assertThat(financialProduct.dclsEndDay).isEqualTo("20220930")
        assertThat(financialProduct.financialSubmitDay).isEqualTo("20220910")
        assertThat(financialProduct.financialCompany?.companyName).isEqualTo("우리은행")
        assertThat(financialProduct.financialProductOptions).hasSize(1)

        val financialProductOption = financialProduct.financialProductOptions.first()
        assertThat(financialProductOption.interestRateType).isEqualTo(InterestRateType.SIMPLE)
        assertThat(financialProductOption.depositPeriodMonths).isEqualTo("3")
        assertThat(financialProductOption.baseInterestRate).isEqualTo(BigDecimal.valueOf(3.00))
        assertThat(financialProductOption.maximumInterestRate).isEqualTo(BigDecimal.valueOf(3.4))
    }

    @Test
    fun findFinancialProductsWithPaginationInfo_success_by_sort() {
        // Given
        val financialCompanyEntity =
            FinancialCompanyEntity(
                financialCompanyCode = "0010001",
                companyName = "우리은행",
                financialGroupType = FinancialGroupType.BANK,
                dclsChrgMan = "영업맨",
                hompUrl = "https://www.woori.co.kr",
                calTel = "123-4567-8901",
                dclsMonth = "202209",
            )
        financialCompanyRepository.save(financialCompanyEntity)
        val financialProductEntity =
            FinancialProductEntity(
                financialProductCode = "WR0001B",
                financialProductName = "첫적금",
                joinWay = "일반",
                postMaturityInterestRate = "1년후 0.2",
                specialCondition = "우대방법",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                joinMember = "A123456",
                additionalNotes = "적금 3개월",
                financialCompanyEntity = financialCompanyEntity,
                dclsMonth = "202209",
                dclsStartDay = "20220901",
                dclsEndDay = "20220930",
                financialSubmitDay = "20220910",
            )
        financialProductRepository.save(financialProductEntity)
        val financialProductOptionEntity1 =
            FinancialProductOptionEntity(
                financialProductEntity = financialProductEntity,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = null,
                depositPeriodMonths = "3",
                baseInterestRate = BigDecimal.valueOf(3.00),
                maximumInterestRate = BigDecimal.valueOf(3.4),
            )
        val financialProductOptionEntity2 =
            FinancialProductOptionEntity(
                financialProductEntity = financialProductEntity,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = null,
                depositPeriodMonths = "6",
                baseInterestRate = BigDecimal.valueOf(3.00),
                maximumInterestRate = BigDecimal.valueOf(3.4),
            )
        financialProductOptionRepository.saveAll(listOf(financialProductOptionEntity1, financialProductOptionEntity2))

        val financialCompanyEntity2 =
            FinancialCompanyEntity(
                financialCompanyCode = "0010002",
                companyName = "국민은행",
                financialGroupType = FinancialGroupType.BANK,
                dclsChrgMan = "영업맨",
                hompUrl = "https://www.kb.co.kr",
                calTel = "123-4567-8901",
                dclsMonth = "202209",
            )
        financialCompanyRepository.save(financialCompanyEntity2)
        val financialProductEntity2 =
            FinancialProductEntity(
                financialProductCode = "KB0001B",
                financialProductName = "KB적금",
                joinWay = "일반",
                postMaturityInterestRate = "1년후 0.2",
                specialCondition = "우대방법",
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                joinMember = "A123456",
                additionalNotes = "적금 3개월",
                financialCompanyEntity = financialCompanyEntity2,
                dclsMonth = "202209",
                dclsStartDay = "20220901",
                dclsEndDay = "20220930",
                financialSubmitDay = "20220910",
            )
        financialProductRepository.save(financialProductEntity2)
        val financialProductOptionEntity3 =
            FinancialProductOptionEntity(
                financialProductEntity = financialProductEntity2,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = null,
                depositPeriodMonths = "3",
                baseInterestRate = BigDecimal.valueOf(3.00),
                maximumInterestRate = BigDecimal.valueOf(3.9),
            )
        val financialProductOptionEntity4 =
            FinancialProductOptionEntity(
                financialProductEntity = financialProductEntity2,
                interestRateType = InterestRateType.SIMPLE,
                reserveType = null,
                depositPeriodMonths = "6",
                baseInterestRate = BigDecimal.valueOf(3.00),
                maximumInterestRate = BigDecimal.valueOf(3.7),
            )
        financialProductOptionRepository.saveAll(listOf(financialProductOptionEntity3, financialProductOptionEntity4))

        // When
        val financialProducts =
            getFinancialProductPort.findFinancialProductsWithPaginationInfo(
                financialGroupType = FinancialGroupType.BANK,
                companyName = null,
                joinRestriction = JoinRestriction.NO_RESTRICTION,
                financialProductType = FinancialProductType.SAVINGS,
                financialProductName = null,
                depositPeriodMonths = "3",
                pageable = PageRequest.of(0, 10, Sort.by(listOf(Sort.Order(Sort.Direction.ASC, "maximumInterestRate")))),
            )
        // Then
        assertThat(financialProducts.first).hasSize(2)
        assertThat(financialProducts.second).isFalse()

        val financialProduct = financialProducts.first.first()
        assertThat(financialProduct.financialProductCode).isEqualTo("WR0001B")
        assertThat(financialProduct.financialProductName).isEqualTo("첫적금")
        assertThat(financialProduct.joinWay).isEqualTo("일반")
        assertThat(financialProduct.postMaturityInterestRate).isEqualTo("1년후 0.2")
        assertThat(financialProduct.specialCondition).isEqualTo("우대방법")
        assertThat(financialProduct.joinRestriction).isEqualTo(JoinRestriction.NO_RESTRICTION)
        assertThat(financialProduct.financialProductType).isEqualTo(FinancialProductType.SAVINGS)
        assertThat(financialProduct.joinMember).isEqualTo("A123456")
        assertThat(financialProduct.additionalNotes).isEqualTo("적금 3개월")
        assertThat(financialProduct.maxLimit).isEqualTo(null)
        assertThat(financialProduct.dclsMonth).isEqualTo("202209")
        assertThat(financialProduct.dclsStartDay).isEqualTo("20220901")
        assertThat(financialProduct.dclsEndDay).isEqualTo("20220930")
        assertThat(financialProduct.financialSubmitDay).isEqualTo("20220910")
        assertThat(financialProduct.financialCompany?.companyName).isEqualTo("우리은행")
        assertThat(financialProduct.financialProductOptions).hasSize(1)

        val financialProductOption = financialProduct.financialProductOptions.first()
        assertThat(financialProductOption.interestRateType).isEqualTo(InterestRateType.SIMPLE)
        assertThat(financialProductOption.depositPeriodMonths).isEqualTo("3")
        assertThat(financialProductOption.baseInterestRate).isEqualTo(BigDecimal.valueOf(3.00))
        assertThat(financialProductOption.maximumInterestRate).isEqualTo(BigDecimal.valueOf(3.4))
    }
}
