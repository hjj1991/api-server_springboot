package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountBookRepository extends JpaRepository<AccountBookEntity, Long> {

    @Query("select ab from AccountBookEntity ab where ab.accountBookNo = " +
            "(select abu.accountBookInfo.accountBookNo from AccountBookUserEntity abu " +
            "where abu.userInfo.userNo = :userNo and abu.accountBookInfo.accountBookNo = :accountBookNo and abu.accountRole = :accountRole)")
    Optional<AccountBookEntity> findAccountBookBySubQuery(@Param("userNo") Long userNo, @Param("accountBookNo")Long accountBookNo, @Param("accountRole") AccountBookUserEntity.AccountRole accountRole);

    @Query("select ab from AccountBookEntity ab where ab.accountBookNo = " +
            "(select abu.accountBookInfo.accountBookNo from AccountBookUserEntity abu " +
            "where abu.userInfo.userNo = :userNo and abu.accountBookInfo.accountBookNo = :accountBookNo and abu.accountRole in ('OWNER', 'MEMBER'))")
    Optional<AccountBookEntity> findAccountBookBySubQuery(@Param("userNo") Long userNo, @Param("accountBookNo") Long accountBookNo);

}
