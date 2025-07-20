package com.hjj.apiserver.adapter.out.persistence.financial

import com.hjj.apiserver.adapter.out.persistence.financial.document.FinancialProductDocument
import com.hjj.apiserver.application.port.out.financial.SearchFinancialProductPort
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Component

@Component
class FinancialProductSearchAdapter(
    private val elasticsearchOperations: ElasticsearchOperations,
) : SearchFinancialProductPort {
    override fun searchFinancialProducts(
        financialGroupType: FinancialGroupType?,
        companyName: String?,
        joinRestriction: JoinRestriction?,
        financialProductType: FinancialProductType?,
        financialProductName: String?,
        depositPeriodMonths: String?,
        query: String?,
        pageable: Pageable,
    ): Slice<Long> {
        // 1) var 로 선언
        var criteria = Criteria()

        // 2) 각 조건 재할당
        financialGroupType?.let {
            criteria = criteria.and("financialGroupType").`is`(it.name)
        }
        companyName?.let {
            criteria = criteria.and("companyName").matches(it)
        }
        joinRestriction?.let {
            criteria = criteria.and("joinRestriction").`is`(it.name)
        }
        financialProductType?.let {
            criteria = criteria.and("financialProductType").`is`(it.name)
        }
        financialProductName?.let {
            criteria = criteria.and("productName").matches(it)
        }
        depositPeriodMonths?.let {
            // nested 필터도 동일하게 재할당
            criteria = criteria.and("options.depositPeriodMonths").`is`(it)
        }

        if (!query.isNullOrBlank()) {
            val q = Criteria.where("productName").matches(query)
                .or("companyName").matches(query)
                .or("specialCondition").matches(query)
                .or("joinWay").matches(query)
                .or("etcNote").matches(query)
            criteria = criteria.and(q)
        }

        // 3) 최종 쿼리 실행
        val springDataQuery = CriteriaQuery(criteria, pageable)
        val searchHits = elasticsearchOperations.search(springDataQuery, FinancialProductDocument::class.java)
        val ids = searchHits.map { it.content.financialProductId }.toList()
        val hasNext = SearchHitSupport.searchPageFor(searchHits, pageable).hasNext()

        return SliceImpl(ids, pageable, hasNext)
    }
}
