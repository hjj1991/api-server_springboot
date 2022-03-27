package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.CategoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryEntity, Long> {

    @Query("select c from CategoryEntity c where c.accountBookInfo.accountBookNo = " +
            "(select abu.accountBookInfo.accountBookNo from AccountBookUserEntity abu " +
            "where abu.accountBookInfo.accountBookNo = :accountBookNo and abu.userInfo.userNo = :userNo and abu.accountRole <> 'GUEST')")
    @EntityGraph(attributePaths = {"parentCategory"})
    List<CategoryEntity> findEntityGraphBySubQuery(@Param("accountBookNo") Long accountBookNo, @Param("userNo") Long userNo);

    @Query("select c from CategoryEntity c where c.categoryNo = :categoryNo and c.accountBookInfo.accountBookNo = " +
            "(select abu.accountBookInfo.accountBookNo from AccountBookUserEntity abu " +
            "where abu.accountBookInfo.accountBookNo = :accountBookNo and abu.userInfo.userNo = :userNo and abu.accountRole in :accountRole)")
    Optional<CategoryEntity> findByCategoryNoAndSubQuery(@Param("categoryNo") Long categoryNo, @Param("accountBookNo") Long accountBookNo, @Param("userNo") Long userNo, @Param("accountRole")List<AccountBookUserEntity.AccountRole> accountRole);
}
