package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialCompanyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialCompanyRepository : JpaRepository<FinancialCompanyEntity, Long>
