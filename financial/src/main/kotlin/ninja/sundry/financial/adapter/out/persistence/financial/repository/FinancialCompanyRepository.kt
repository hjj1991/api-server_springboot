package ninja.sundry.financial.adapter.out.persistence.financial.repository

import ninja.sundry.financial.adapter.out.persistence.financial.entity.FinancialCompanyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FinancialCompanyRepository : JpaRepository<FinancialCompanyEntity, Long>
