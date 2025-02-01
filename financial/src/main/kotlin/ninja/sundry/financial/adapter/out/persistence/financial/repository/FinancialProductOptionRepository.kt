package ninja.sundry.financial.adapter.out.persistence.financial.repository

import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialProductOptionRepository : JpaRepository<FinancialProductOptionEntity, Long>
