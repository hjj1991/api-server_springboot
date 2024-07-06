package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialProductOptionRepository : JpaRepository<FinancialProductOptionEntity, Long>
