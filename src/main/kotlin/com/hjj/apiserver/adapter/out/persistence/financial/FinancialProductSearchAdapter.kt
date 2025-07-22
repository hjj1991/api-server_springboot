package com.hjj.apiserver.adapter.out.persistence.financial

import co.elastic.clients.elasticsearch._types.SortMode
import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.hjj.apiserver.adapter.out.persistence.financial.document.FinancialProductDocument
import com.hjj.apiserver.application.port.out.financial.SearchFinancialProductPort
import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
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
        pageable: Pageable,
    ): Slice<Long> {
        val queries = mutableListOf<Query>()

        financialGroupType?.let {
            queries.add(Query.Builder().term { t -> t.field("financialGroupType").value(it.name) }.build())
        }
        companyName?.let {
            queries.add(Query.Builder().match { m -> m.field("companyName").query(it) }.build())
        }
        joinRestriction?.let {
            queries.add(Query.Builder().term { t -> t.field("joinRestriction").value(it.name) }.build())
        }
        financialProductType?.let {
            queries.add(Query.Builder().term { t -> t.field("financialProductType").value(it.name) }.build())
        }
        financialProductName?.let {
            queries.add(Query.Builder().match { m -> m.field("productName").query(it) }.build())
        }
        depositPeriodMonths?.let {
            queries.add(
                Query.Builder().nested { n ->
                    n.path("options")
                        .query { q -> q.term { t -> t.field("options.depositPeriodMonths").value(it) } }
                }.build(),
            )
        }

        val boolQuery = Query.Builder().bool { b -> b.must(queries) }.build()

        val sortOptions =
            pageable.sort.map { order ->
                val field = order.property
                val direction = if (order.isAscending) SortOrder.Asc else SortOrder.Desc

                if (field.startsWith("options.")) {
                    SortOptions.Builder().field {
                        it.field(field)
                            .order(direction)
                            .mode(SortMode.Max)
                            .nested { nested ->
                                nested.path("options").filter { f -> f.matchAll { m -> m } }
                            }
                    }.build()
                } else {
                    val sortField = when (field) {
                        "productName", "companyName" -> "$field.keyword"
                        else -> field
                    }
                    SortOptions.Builder().field { it.field(sortField).order(direction) }.build()
                }
            }.toList()

        // Manually handle pagination to avoid sort conflicts with withPageable
        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize)

        val nativeQueryBuilder =
            NativeQuery.builder()
                .withQuery(if (queries.isEmpty()) Query.Builder().matchAll { m -> m }.build() else boolQuery)
                .withPageable(pageRequest) // Use pageable without sort info

        if (sortOptions.isNotEmpty()) {
            nativeQueryBuilder.withSort(sortOptions)
        }

        val nativeQuery = nativeQueryBuilder.build()

        val searchHits = elasticsearchOperations.search(nativeQuery, FinancialProductDocument::class.java)
        val ids = searchHits.map { it.content.financialProductId }.toList()
        // Important: Use the original pageable for the Slice response
        val hasNext = SearchHitSupport.searchPageFor(searchHits, pageable).hasNext()

        return SliceImpl(ids, pageable, hasNext)
    }
}
