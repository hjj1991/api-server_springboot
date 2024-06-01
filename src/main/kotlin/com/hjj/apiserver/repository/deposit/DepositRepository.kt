package com.hjj.apiserver.repository.deposit

import com.hjj.apiserver.domain.deposit.Deposit
import com.hjj.apiserver.domain.deposit.DepositPK
import org.springframework.data.jpa.repository.JpaRepository

interface DepositRepository : JpaRepository<Deposit, DepositPK>, DepositRepositoryCustom
