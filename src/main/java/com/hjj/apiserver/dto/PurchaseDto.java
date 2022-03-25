package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.CategoryEntity;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PurchaseDto {

    private Long purchaseNo;
    private Long userNo;
    private Long cardNo;
    private Long storeNo;
    private String storeName;
    private PurchaseEntity.PurchaseType purchaseType;
    private int price;
    private String reason;
    private LocalDate purchaseDate;
    private CardEntity cardInfo;
    private CategoryEntity categoryInfo;
    private UserEntity userInfo;

    @Data
    public static class RequestGetPurchaseListForm {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Data
    public static class ResponseGetPurchase {
        private Long purchaseNo;
        private Long userNo;
        private Long cardNo;
        private Long categoryNo;
        private PurchaseEntity.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;
        private CardDto cardInfo;
        private CategoryDto category;

    }


    @Data
    public static class RequestAddPurchaseForm {
        private String storeName;
        private PurchaseEntity.PurchaseType purchaseType;
        private int price;
        private String reason;
        private Long cardNo;
        private Long storeNo;
        private LocalDate purchaseDate;

    }

    public PurchaseEntity toEntity(){
        PurchaseEntity purchaseEntity = PurchaseEntity.builder()
                .storeName(storeName)
                .purchaseType(purchaseType)
                .price(price)
                .reason(reason)
                .purchaseDate(purchaseDate)
                .cardInfo(cardInfo)
                .userInfo(userInfo)
                .build();
        /* 연관관계 편의 메소드 */
        if(categoryInfo != null){
            purchaseEntity.changeCategoryInfo(categoryInfo);
        }
        return purchaseEntity;
    }
}
