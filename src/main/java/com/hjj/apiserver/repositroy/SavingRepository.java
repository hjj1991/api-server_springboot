package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.saving.Saving;
import com.hjj.apiserver.domain.saving.SavingPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, SavingPK>, SavingRepositoryCustom {
}
