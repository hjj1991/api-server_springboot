package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBookUserRepository extends JpaRepository<AccountBookUserEntity, Long> {

    @EntityGraph(attributePaths = {"accountBookEntity", "userEntity"})
    List<AccountBookUserEntity> findEntityGraphByUserEntity_userNo(Long userNo);
    @EntityGraph(attributePaths = {"accountBookEntity", "userEntity"})
    List<AccountBookUserEntity> findEntityGraphByAccountBookEntity_accountBookNoIn(List<Long> accountBookNoList);
    Optional<AccountBookUserEntity> findByUserEntity_UserNoAndAccountBookEntity_AccountBookNo(Long userNo, Long accountBookNo);

    Boolean existsByUserEntity_UserNoAndAccountBookEntity_AccountBookNoAndAccountRole(Long userNo, Long accountBookNo, AccountBookUserEntity.AccountRole accountRole);
}
