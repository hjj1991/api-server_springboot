package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.PurchaseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    @EntityGraph(attributePaths = {"cardInfo", "storeInfo"})
    List<PurchaseEntity> findAllEntityGraphByPurchaseDateBetweenAndUserInfo_UserNoAndDeleteYnOrderByPurchaseDateDesc(LocalDate startDate, LocalDate endDate, Long userNo, char deleteYn);

    @EntityGraph(attributePaths = {"userInfo"})
    PurchaseEntity findEntityGraphByUserInfo_UserNoAndPurchaseNoAndDeleteYn(Long userNo, Long purchaseNo, char deleteYn);


}
