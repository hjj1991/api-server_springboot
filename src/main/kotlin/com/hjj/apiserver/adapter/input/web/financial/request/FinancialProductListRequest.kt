package com.hjj.apiserver.adapter.input.web.financial.request

import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import jakarta.validation.constraints.Pattern

class FinancialProductListRequest {
    var financialGroupType: FinancialGroupType? = null
    var companyName: String? = null
    var joinRestriction: JoinRestriction? = null
    var financialProductType: FinancialProductType? = null
    var financialProductName: String? = null
    var q: String? = null
    var status: ProductStatus = ProductStatus.ACTIVE

    @field:Pattern(
        regexp = DEPOSIT_PERIOD_MONTHS_PATTERN,
        message = INVALID_DEPOSIT_PERIOD_MONTHS_MESSAGE,
    )
    var depositPeriodMonths: String? = null

    companion object {
        const val DEPOSIT_PERIOD_MONTHS_PATTERN = "^[1-9]\\d{0,2}$"
        const val INVALID_DEPOSIT_PERIOD_MONTHS_MESSAGE = "depositPeriodMonths는 1~3자리 숫자여야 합니다."
    }
}
