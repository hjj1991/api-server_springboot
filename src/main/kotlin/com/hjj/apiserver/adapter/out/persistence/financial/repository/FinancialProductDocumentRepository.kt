package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.document.FinancialProductDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface FinancialProductDocumentRepository : ElasticsearchRepository<FinancialProductDocument, String>
