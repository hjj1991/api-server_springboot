package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.PurchaseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    @EntityGraph(attributePaths = {"cardInfo", "categoryInfo"})
    List<PurchaseEntity> findAllEntityGraphByPurchaseDateBetweenAndAccountBookInfo_AccountBookNoOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate, Long accountBookNo);

    @EntityGraph(attributePaths = {"cardInfo", "categoryInfo"})
    List<PurchaseEntity> findAllEntityGraphByPurchaseDateBetweenAndUserInfo_UserNoOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate, Long userNo);

    @EntityGraph(attributePaths = {"userInfo"})
    PurchaseEntity findEntityGraphByUserInfo_UserNoAndPurchaseNoAndDeleteYn(Long userNo, Long purchaseNo, char deleteYn);

    List<PurchaseEntity> findByCategoryInfo_CategoryNo(Long categoryNo);

    @Modifying(clearAutomatically = true)
    @Query("update PurchaseEntity set categoryInfo = null where categoryInfo.categoryNo = :categoryNo")
    void deleteCategoryAllPurchaseEntityByCategoryNo(@Param("categoryNo") Long categoryNo);


}
