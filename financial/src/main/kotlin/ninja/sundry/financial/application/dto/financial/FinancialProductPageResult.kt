package ninja.sundry.financial.application.dto.financial

import domain.financial.FinancialProduct

data class FinancialProductPageResult(
    val financialProducts: List<FinancialProduct>,
    val hasMore: Boolean
)
