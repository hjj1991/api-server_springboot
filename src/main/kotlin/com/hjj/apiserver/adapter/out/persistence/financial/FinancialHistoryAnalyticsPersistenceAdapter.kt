package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.application.port.out.financial.GetFinancialAnalyticsPort
import com.hjj.apiserver.application.port.out.financial.GetFinancialHistoryPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.dto.financial.FinancialProductChangeSummaryDto
import com.hjj.apiserver.dto.financial.FinancialProductHistoryDto
import com.hjj.apiserver.dto.financial.FinancialProductHistorySliceDto
import com.hjj.apiserver.dto.financial.FinancialProductRateHistoryDto
import com.hjj.apiserver.dto.financial.FinancialRateSeriesPointDto
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.util.Base64

@PersistenceAdapter
class FinancialHistoryAnalyticsPersistenceAdapter(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) : GetFinancialHistoryPort, GetFinancialAnalyticsPort {
    override fun findFinancialProductHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto {
        val fetchLimit = limit + 1
        val cursorKey = decodeCursor(cursor)
        val params =
            MapSqlParameterSource()
                .addValue("financialProductId", financialProductId)
                .addValue("cursorObservedAt", cursorKey?.observedAt)
                .addValue("cursorProductId", cursorKey?.financialProductId)
                .addValue("fetchLimit", fetchLimit)

        val rows =
            jdbcTemplate.query(
                """
                SELECT observed_at,
                       financial_product_id,
                       financial_company_id,
                       financial_product_code,
                       financial_product_type,
                       status,
                       product_content_hash,
                       payload::text AS payload
                FROM financial_product_history
                WHERE financial_product_id = :financialProductId
                  AND (
                      :cursorObservedAt IS NULL
                      OR (observed_at, financial_product_id) < (:cursorObservedAt, :cursorProductId)
                  )
                ORDER BY observed_at DESC, financial_product_id DESC
                LIMIT :fetchLimit
                """.trimIndent(),
                params,
            ) { rs, _ ->
                FinancialProductHistoryDto(
                    observedAt = rs.getObject("observed_at", OffsetDateTime::class.java),
                    financialProductId = rs.getLong("financial_product_id"),
                    financialCompanyId = rs.getLong("financial_company_id"),
                    financialProductCode = rs.getString("financial_product_code"),
                    financialProductType = rs.getString("financial_product_type"),
                    status = rs.getString("status"),
                    productContentHash = rs.getString("product_content_hash"),
                    payload = rs.getString("payload"),
                )
            }

        return toHistorySlice(rows, limit)
    }

    override fun findFinancialProductRateHistoriesByProductId(
        financialProductId: Long,
        cursor: String?,
        limit: Int,
    ): List<FinancialProductRateHistoryDto> {
        val cursorKey = decodeCursor(cursor)
        val params =
            MapSqlParameterSource()
                .addValue("financialProductId", financialProductId)
                .addValue("cursorObservedAt", cursorKey?.observedAt)
                .addValue("limit", limit)

        return jdbcTemplate.query(
            """
            SELECT observed_at,
                   financial_product_id,
                   financial_product_option_id,
                   interest_rate_type,
                   reserve_type,
                   deposit_period_months,
                   base_interest_rate,
                   maximum_interest_rate,
                   payload::text AS payload
            FROM financial_product_rate_history
            WHERE financial_product_id = :financialProductId
              AND (
                  :cursorObservedAt IS NULL
                  OR observed_at < :cursorObservedAt
              )
            ORDER BY observed_at DESC
            LIMIT :limit
            """.trimIndent(),
            params,
        ) { rs, _ ->
            FinancialProductRateHistoryDto(
                observedAt = rs.getObject("observed_at", OffsetDateTime::class.java),
                financialProductId = rs.getLong("financial_product_id"),
                financialProductOptionId = rs.getObject("financial_product_option_id")?.let { (it as Number).toLong() },
                interestRateType = rs.getString("interest_rate_type"),
                reserveType = rs.getString("reserve_type"),
                depositPeriodMonths = rs.getInt("deposit_period_months"),
                baseInterestRate = rs.getBigDecimal("base_interest_rate"),
                maximumInterestRate = rs.getBigDecimal("maximum_interest_rate"),
                payload = rs.getString("payload"),
            )
        }
    }

    override fun findFinancialProductHistories(
        cursor: String?,
        limit: Int,
    ): FinancialProductHistorySliceDto {
        val fetchLimit = limit + 1
        val cursorKey = decodeCursor(cursor)
        val params =
            MapSqlParameterSource()
                .addValue("cursorObservedAt", cursorKey?.observedAt)
                .addValue("cursorProductId", cursorKey?.financialProductId)
                .addValue("fetchLimit", fetchLimit)

        val rows =
            jdbcTemplate.query(
                """
                SELECT observed_at,
                       financial_product_id,
                       financial_company_id,
                       financial_product_code,
                       financial_product_type,
                       status,
                       product_content_hash,
                       payload::text AS payload
                FROM financial_product_history
                WHERE (
                    :cursorObservedAt IS NULL
                    OR (observed_at, financial_product_id) < (:cursorObservedAt, :cursorProductId)
                )
                ORDER BY observed_at DESC, financial_product_id DESC
                LIMIT :fetchLimit
                """.trimIndent(),
                params,
            ) { rs, _ ->
                FinancialProductHistoryDto(
                    observedAt = rs.getObject("observed_at", OffsetDateTime::class.java),
                    financialProductId = rs.getLong("financial_product_id"),
                    financialCompanyId = rs.getLong("financial_company_id"),
                    financialProductCode = rs.getString("financial_product_code"),
                    financialProductType = rs.getString("financial_product_type"),
                    status = rs.getString("status"),
                    productContentHash = rs.getString("product_content_hash"),
                    payload = rs.getString("payload"),
                )
            }

        return toHistorySlice(rows, limit)
    }

    override fun findRateSeries(
        from: OffsetDateTime,
        to: OffsetDateTime,
        bucket: String,
        financialProductId: Long?,
        limit: Int,
    ): List<FinancialRateSeriesPointDto> {
        val params =
            MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("bucket", bucket)
                .addValue("financialProductId", financialProductId)
                .addValue("limit", limit)

        return jdbcTemplate.query(
            """
            SELECT time_bucket(CAST(:bucket AS interval), observed_at) AS bucket,
                   AVG(base_interest_rate) AS average_base_interest_rate,
                   MAX(maximum_interest_rate) AS maximum_interest_rate
            FROM financial_product_rate_history
            WHERE observed_at >= :from
              AND observed_at <= :to
              AND (:financialProductId IS NULL OR financial_product_id = :financialProductId)
            GROUP BY bucket
            ORDER BY bucket DESC
            LIMIT :limit
            """.trimIndent(),
            params,
        ) { rs, _ ->
            FinancialRateSeriesPointDto(
                bucket = rs.getObject("bucket", OffsetDateTime::class.java),
                averageBaseInterestRate = rs.getBigDecimal("average_base_interest_rate")?.setScale(5, RoundingMode.HALF_UP),
                maximumInterestRate = rs.getBigDecimal("maximum_interest_rate"),
            )
        }
    }

    override fun findProductChanges(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<FinancialProductChangeSummaryDto> {
        val params =
            MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)

        return jdbcTemplate.query(
            """
            SELECT status, COUNT(*) AS count
            FROM financial_product_history
            WHERE observed_at >= :from
              AND observed_at <= :to
            GROUP BY status
            ORDER BY status ASC
            """.trimIndent(),
            params,
        ) { rs, _ ->
            FinancialProductChangeSummaryDto(
                status = rs.getString("status"),
                count = rs.getLong("count"),
            )
        }
    }

    private fun toHistorySlice(
        rows: List<FinancialProductHistoryDto>,
        limit: Int,
    ): FinancialProductHistorySliceDto {
        val items = if (rows.size > limit) rows.take(limit) else rows
        val nextCursor =
            if (rows.size > limit) {
                val last = items.last()
                encodeCursor(last.observedAt, last.financialProductId)
            } else {
                null
            }

        return FinancialProductHistorySliceDto(
            items = items,
            nextCursor = nextCursor,
        )
    }

    private fun encodeCursor(
        observedAt: OffsetDateTime,
        financialProductId: Long,
    ): String {
        val raw = "$observedAt|$financialProductId"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.toByteArray(StandardCharsets.UTF_8))
    }

    private fun decodeCursor(cursor: String?): CursorKey? {
        if (cursor.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val decoded = String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8)
            val tokens = decoded.split("|")
            CursorKey(
                observedAt = OffsetDateTime.parse(tokens[0]),
                financialProductId = tokens[1].toLong(),
            )
        }.getOrNull()
    }

    private data class CursorKey(
        val observedAt: OffsetDateTime,
        val financialProductId: Long,
    )
}
