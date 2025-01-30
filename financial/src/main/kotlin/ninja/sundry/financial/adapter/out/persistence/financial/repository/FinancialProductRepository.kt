package com.hjj.apiserver.adapter.out.persistence.financial.repository

import com.hjj.apiserver.adapter.out.persistence.financial.entity.FinancialProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialProductRepository : JpaRepository<FinancialProductEntity, Long>
