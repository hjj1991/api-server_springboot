package ninja.sundry.financial.application.port.out.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProduct
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable

interface GetFinancialProductPort {
    fun findFinancialProductsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Pair<List<FinancialProduct>, Boolean>

    fun findFinancialProduct(financialProductId: Long): FinancialProduct
}
