package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntityJava;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBookUserRepository extends JpaRepository<AccountBookUserEntityJava, Long> {

    @EntityGraph(attributePaths = {"accountBookEntity", "userEntity"})
    List<AccountBookUserEntityJava> findEntityGraphByUserEntity_userNo(Long userNo);
    @EntityGraph(attributePaths = {"accountBookEntity", "userEntity"})
    List<AccountBookUserEntityJava> findEntityGraphByAccountBookEntity_accountBookNoIn(List<Long> accountBookNoList);
    Optional<AccountBookUserEntityJava> findByUserEntity_UserNoAndAccountBookEntity_AccountBookNo(Long userNo, Long accountBookNo);

    Boolean existsByUserEntity_UserNoAndAccountBookEntity_AccountBookNoAndAccountRole(Long userNo, Long accountBookNo, AccountBookUserEntityJava.AccountRole accountRole);
}
