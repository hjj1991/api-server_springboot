package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.PurchaseEntityJava;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntityJava, Long>, PurchaseRepositoryCustom {

    @EntityGraph(attributePaths = {"cardEntity", "categoryEntity"})
    List<PurchaseEntityJava> findAllEntityGraphByPurchaseDateBetweenAndAccountBookEntity_AccountBookNoAndDeleteYnOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate, Long accountBookNo, char deleteYn);

    @EntityGraph(attributePaths = {"cardEntity", "categoryEntity"})
    List<PurchaseEntityJava> findAllEntityGraphByPurchaseDateBetweenAndUserEntity_UserNoOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate, Long userNo);

    @EntityGraph(attributePaths = {"userEntity"})
    PurchaseEntityJava findEntityGraphByUserEntity_UserNoAndPurchaseNoAndDeleteYn(Long userNo, Long purchaseNo, char deleteYn);

    List<PurchaseEntityJava> findByCategoryEntity_CategoryNo(Long categoryNo);

    @Modifying(clearAutomatically = true)
    @Query("update PurchaseEntityJava set categoryEntity = null where categoryEntity.categoryNo = :categoryNo")
    void deleteCategoryAllPurchaseEntityByCategoryNo(@Param("categoryNo") Long categoryNo);


}
