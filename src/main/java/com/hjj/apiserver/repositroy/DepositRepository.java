package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.Deposit;
import com.hjj.apiserver.domain.DepositPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, DepositPK>, DepositRepositoryCustom {
}
