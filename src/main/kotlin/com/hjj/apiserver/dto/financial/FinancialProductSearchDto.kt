package com.hjj.apiserver.dto.financial

data class ProductSearchResponse(
    val displayResponse: String,
    val debugLogs: DebugLogs,
)

data class DebugLogs(
    val originalQuery: String,
    val rewrittenQuery: String?,
    val searchResults: List<Map<String, Any>>,
)

data class FinancialProductSearchResponse(
    val displayResponse: String,
)
