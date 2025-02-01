package ninja.sundry.front.financial

import domain.financial.FinancialGroupType
import domain.financial.FinancialProductType
import domain.financial.JoinRestriction
import model.SliceResponse
import ninja.sundry.front.financial.model.response.FinancialProductResponse
import org.springframework.grpc.server.service.GrpcService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/financials")
@GrpcService
class FinancialController(
) {

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
    ): SliceResponse<FinancialProductResponse> {

        TODO()
    }
}
