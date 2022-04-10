package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBookUserRepository extends JpaRepository<AccountBookUserEntity, Long> {

    @EntityGraph(attributePaths = {"accountBookInfo", "userInfo"})
    List<AccountBookUserEntity> findEntityGraphByUserInfo_userNo(Long userNo);
    @EntityGraph(attributePaths = {"accountBookInfo", "userInfo"})
    List<AccountBookUserEntity> findEntityGraphByAccountBookInfo_accountBookNoIn(List<Long> accountBookNoList);
    Optional<AccountBookUserEntity> findByUserInfo_UserNoAndAccountBookInfo_AccountBookNo(Long userNo, Long accountBookNo);

    Boolean existsByUserInfo_UserNoAndAccountBookInfo_AccountBookNoAndAccountRole(Long userNo, Long accountBookNo, AccountBookUserEntity.AccountRole accountRole);
}
