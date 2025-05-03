package ninja.sundry.financial.application.port.out.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProduct
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import ninja.sundry.financial.adapter.out.persistence.financial.dto.FinancialProductSearchCondition
import org.springframework.data.domain.Pageable

interface GetFinancialProductPort {
    fun findFinancialProductsWithPagination(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): List<FinancialProduct>

    fun hasNextFinancialProductsWithPagination(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Boolean

    fun findFinancialProduct(financialProductId: Long): FinancialProduct
}
