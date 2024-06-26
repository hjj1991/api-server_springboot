package com.hjj.apiserver.dto.purchase.request

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class PurchaseFindOfPageRequest(
    val accountBookNo: Long,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate,
    val size: Int = PURCHASE_DEFAULT_SIZE,
    val page: Int = 0,
) {
    companion object {
        const val PURCHASE_DEFAULT_SIZE = 100
    }

    fun getPageRequest(): Pageable {
        return PageRequest.of(page, size)
    }
}
