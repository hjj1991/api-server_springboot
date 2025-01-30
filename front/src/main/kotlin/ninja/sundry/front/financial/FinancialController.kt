package ninja.sundry.front.financial

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/financials")
class FinancialController {

    @GetMapping
    suspend fun getFinancials(
        @RequestParam(required = false) financialGroupType: FinancialGroupType?,
        @RequestParam(required = false) companyName: String?,
        @RequestParam(required = false) joinRestriction: JoinRestriction?,
        @RequestParam(required = false) financialProductType: FinancialProductType?,
        @RequestParam(required = false) financialProductName: String?,
        @RequestParam(required = false) depositPeriodMonths: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): List<FinancialProductResponse> {
        val pageable = PageRequest.of(page, size)

        val (financialProducts, hasNext) =
            getFinancialUseCase.getFinancialsWithPaginationInfo(
                financialGroupType = financialGroupType,
                companyName = companyName,
                joinRestriction = joinRestriction,
                financialProductType = financialProductType,
                financialProductName = financialProductName,
                depositPeriodMonths = depositPeriodMonths,
                pageable = pageable
            )

        return financialProducts.map { FinancialProductResponse.from(it) }
    }
}
