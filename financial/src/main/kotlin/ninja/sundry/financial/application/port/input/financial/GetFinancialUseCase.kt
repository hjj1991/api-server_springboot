package ninja.sundry.financial.application.port.input.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProduct
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable

interface GetFinancialUseCase {
    fun getFinancialsWithPaginationInfo(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        pageable: Pageable,
    ): Pair<List<FinancialProduct>, Boolean>

    fun getFinancialProduct(financialProductId: Long): FinancialProduct
}
