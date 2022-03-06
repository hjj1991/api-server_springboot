package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    @Query(" select p from PurchaseEntity p left join fetch p.cardInfo left join fetch p.storeInfo where p.purchaseDate between :startDate and :endDate and p.deleteYn = 'N' order by p.purchaseDate desc")
    List<PurchaseEntity> findAllFetchJoinByStartDateAndEndDate(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    @Query(" select p from PurchaseEntity p join fetch p.userInfo where p.userInfo.userNo = :userNo and p.purchaseNo = :purchaseNo and p.deleteYn = 'N'")
    PurchaseEntity findByFechJoinUserNoAndPurchaseNoAndDeleteYn(@Param("userNo")Long userNo, @Param("purchaseNo") Long purchaseNo);


}
