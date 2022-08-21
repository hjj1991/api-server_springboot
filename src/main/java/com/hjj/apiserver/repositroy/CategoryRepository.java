package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntityJava;
import com.hjj.apiserver.domain.CategoryEntityJava;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryEntityJava, Long> {

    @Query("select c from CategoryEntityJava c where c.accountBookEntity.accountBookNo = " +
            "(select abu.accountBookEntity.accountBookNo from AccountBookUserEntityJava abu " +
            "where abu.accountBookEntity.accountBookNo = :accountBookNo and abu.userEntity.userNo = :userNo and abu.accountRole <> 'GUEST')")
    @EntityGraph(attributePaths = {"parentCategory"})
    List<CategoryEntityJava> findEntityGraphBySubQuery(@Param("accountBookNo") Long accountBookNo, @Param("userNo") Long userNo);

    @Query("select c from CategoryEntityJava c where c.categoryNo = :categoryNo and c.accountBookEntity.accountBookNo = " +
            "(select abu.accountBookEntity.accountBookNo from AccountBookUserEntityJava abu " +
            "where abu.accountBookEntity.accountBookNo = :accountBookNo and abu.userEntity.userNo = :userNo and abu.accountRole in :accountRole)")
    Optional<CategoryEntityJava> findByCategoryNoAndSubQuery(@Param("categoryNo") Long categoryNo, @Param("accountBookNo") Long accountBookNo, @Param("userNo") Long userNo, @Param("accountRole")List<AccountBookUserEntityJava.AccountRole> accountRole);
}
