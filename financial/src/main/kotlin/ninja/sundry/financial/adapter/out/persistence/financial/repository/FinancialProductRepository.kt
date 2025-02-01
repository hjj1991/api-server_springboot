package ninja.sundry.financial.adapter.out.persistence.financial.repository

import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialProductRepository : JpaRepository<FinancialProductEntity, Long>
