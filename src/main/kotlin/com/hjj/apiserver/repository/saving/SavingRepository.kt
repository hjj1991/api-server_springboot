package com.hjj.apiserver.repository.saving

import com.hjj.apiserver.domain.saving.Saving
import com.hjj.apiserver.domain.saving.SavingPK
import org.springframework.data.jpa.repository.JpaRepository

interface SavingRepository: JpaRepository<Saving, SavingPK>, SavingRepositoryCustom {
}