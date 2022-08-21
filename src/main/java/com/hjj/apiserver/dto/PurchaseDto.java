package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.*;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseDto {

    private final static int PURCHASE_DEFAULT_SIZE = 100;

    private Long purchaseNo;
    private Long userNo;
    private Long cardNo;
    private Long categoryNo;
    private Long accountBookNo;
    private String storeName;
    private PurchaseEntityJava.PurchaseType purchaseType;
    private Integer price;
    private String reason;
    private LocalDate purchaseDate;
    private CardEntityJava cardEntity;
    private CategoryEntityJava categoryEntity;
    private UserEntity userEntity;
    private AccountBookEntityJava accountBookEntity;
    private LocalDate startDate;
    private LocalDate endDate;

    @Data
    public static class RequestPurchaseFindForm {
        private Long accountBookNo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private int size = PURCHASE_DEFAULT_SIZE;
        private int page = 0;
    }


    @Data
    public static class ResponsePurchaseList {
        private String accountBookName;
        private Slice<Purchase> purchaseList;
        private List<CardDto> cardList;
        private CategoryDto.ResponseCategory categoryList;


    }

    @Data
    public static class Purchase{
        private Long purchaseNo;
        private Long userNo;
        private Long cardNo;
        private Long accountBookNo;
        private PurchaseEntityJava.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;
        private CategoryDto.PurchaseCategoryInfo categoryInfo;
    }

    @Data
    public static class ResponsePurchaseDetail {
        private Long accountBookNo;
        private Long cardNo;
        private Long parentCategoryNo;
        private Long categoryNo;
        private String storeName;
        private PurchaseEntityJava.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;

        @QueryProjection
        public ResponsePurchaseDetail(Long accountBookNo, Long cardNo, Long parentCategoryNo, Long categoryNo, String storeName, PurchaseEntityJava.PurchaseType purchaseType, int price, String reason, LocalDate purchaseDate) {
            this.accountBookNo = accountBookNo;
            this.cardNo = cardNo;
            this.parentCategoryNo = parentCategoryNo;
            this.categoryNo = categoryNo;
            this.storeName = storeName;
            this.purchaseType = purchaseType;
            this.price = price;
            this.reason = reason;
            this.purchaseDate = purchaseDate;
        }
    }


    @Data
    public static class RequestAddPurchaseForm {
        private Long accountBookNo;
        private Long cardNo;
        private Long categoryNo;
        private String storeName;
        private PurchaseEntityJava.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;

    }

    @Data
    public static class RequestModifyPurchaseForm {
        private Long accountBookNo;
        private Long cardNo;
        private Long categoryNo;
        private String storeName;
        private PurchaseEntityJava.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;

    }

    public PurchaseEntityJava toEntity(){
        PurchaseEntityJava purchaseEntity = PurchaseEntityJava.builder()
                .storeName(storeName)
                .purchaseType(purchaseType)
                .price(price)
                .reason(reason)
                .purchaseDate(purchaseDate)
                .accountBookEntity(accountBookEntity)
                .cardEntity(cardEntity)
                .userEntity(userEntity)
                .build();
        /* 연관관계 편의 메소드 */
        if(categoryEntity != null){
            purchaseEntity.changeCategoryEntity(categoryEntity);
        }
        return purchaseEntity;
    }
}
