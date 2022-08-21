package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookEntityJava;
import com.hjj.apiserver.domain.AccountBookUserEntityJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountBookRepository extends JpaRepository<AccountBookEntityJava, Long> {

    @Query("select ab from AccountBookEntityJava ab where ab.accountBookNo = " +
            "(select abu.accountBookEntity.accountBookNo from AccountBookUserEntityJava abu " +
            "where abu.userEntity.userNo = :userNo and abu.accountBookEntity.accountBookNo = :accountBookNo and abu.accountRole = :accountRole)")
    Optional<AccountBookEntityJava> findAccountBookBySubQuery(@Param("userNo") Long userNo, @Param("accountBookNo")Long accountBookNo, @Param("accountRole") AccountBookUserEntityJava.AccountRole accountRole);

    @Query("select ab from AccountBookEntityJava ab where ab.accountBookNo = " +
            "(select abu.accountBookEntity.accountBookNo from AccountBookUserEntityJava abu " +
            "where abu.userEntity.userNo = :userNo and abu.accountBookEntity.accountBookNo = :accountBookNo and abu.accountRole in ('OWNER', 'MEMBER'))")
    Optional<AccountBookEntityJava> findAccountBookBySubQuery(@Param("userNo") Long userNo, @Param("accountBookNo") Long accountBookNo);

}
