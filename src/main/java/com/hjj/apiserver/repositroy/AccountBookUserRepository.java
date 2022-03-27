package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountBookUserRepository extends JpaRepository<AccountBookUserEntity, Long> {

    @EntityGraph(attributePaths = {"accountBookInfo", "userInfo"})
    List<AccountBookUserEntity> findEntityGraphByUserInfo_userNo(Long userNo);
    @EntityGraph(attributePaths = {"accountBookInfo", "userInfo"})
    List<AccountBookUserEntity> findEntityGraphByAccountBookInfo_accountBookNoIn(List<Long> accountBookNoList);

    Boolean existsByUserInfo_UserNoAndAccountBookInfo_AccountBookNoAndAccountRole(Long userNo, Long accountBookNo, AccountBookUserEntity.AccountRole accountRole);
}
