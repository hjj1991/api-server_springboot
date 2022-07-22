package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.Saving;
import com.hjj.apiserver.domain.SavingPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, SavingPK>, SavingRepositoryCustom {
}
