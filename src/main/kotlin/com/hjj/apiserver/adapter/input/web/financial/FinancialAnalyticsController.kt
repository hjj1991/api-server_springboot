package com.hjj.apiserver.adapter.input.web.financial

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialProductChangeSummaryResponse
import com.hjj.apiserver.adapter.input.web.financial.response.FinancialRateSeriesPointResponse
import com.hjj.apiserver.application.port.input.financial.GetFinancialAnalyticsUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequestMapping("/analytics")
class FinancialAnalyticsController(
    private val getFinancialAnalyticsUseCase: GetFinancialAnalyticsUseCase,
) {
    @GetMapping("/rate-series", headers = [ApiVersionConstants.HEADER_V1])
    fun getFinancialRateSeries(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: OffsetDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: OffsetDateTime,
        @RequestParam(defaultValue = DEFAULT_BUCKET) bucket: String,
        @RequestParam(required = false) financialProductId: Long?,
        @RequestParam(defaultValue = DEFAULT_ANALYTICS_LIMIT) limit: Int,
    ): List<FinancialRateSeriesPointResponse> {
        val rateSeries = getFinancialAnalyticsUseCase.getRateSeries(from, to, bucket, financialProductId, limit.coerceIn(1, 500))
        return rateSeries.map(FinancialRateSeriesPointResponse::from)
    }

    @GetMapping("/product-changes", headers = [ApiVersionConstants.HEADER_V1])
    fun getFinancialProductChangeSummaries(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: OffsetDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: OffsetDateTime,
    ): List<FinancialProductChangeSummaryResponse> {
        val changeSummaries = getFinancialAnalyticsUseCase.getProductChanges(from, to)
        return changeSummaries.map(FinancialProductChangeSummaryResponse::from)
    }

    private companion object {
        const val DEFAULT_BUCKET = "1 day"
        const val DEFAULT_ANALYTICS_LIMIT = "200"
    }
}
