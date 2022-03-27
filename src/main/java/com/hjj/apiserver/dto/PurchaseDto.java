package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseDto {

    private Long purchaseNo;
    private Long userNo;
    private Long cardNo;
    private Long categoryNo;
    private Long accountBookNo;
    private String storeName;
    private PurchaseEntity.PurchaseType purchaseType;
    private int price;
    private String reason;
    private LocalDate purchaseDate;
    private CardEntity cardInfo;
    private CategoryEntity categoryInfo;
    private UserEntity userInfo;
    private AccountBookEntity accountBookInfo;
    private LocalDate startDate;
    private LocalDate endDate;

    @Data
    public static class RequestPurchaseFindForm {
        private Long accountBookNo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Data
    public static class ResponsePurchaseList {
        private List<Purchase> purchaseList;
        private List<CardDto> cardList;
        private List<CategoryDto.ResponseCategory> categoryList;

        @Data
        public static class Purchase{
            private Long purchaseNo;
            private Long userNo;
            private Long cardNo;
            private Long accountBookNo;
            private PurchaseEntity.PurchaseType purchaseType;
            private int price;
            private String reason;
            private LocalDate purchaseDate;
            private CardDto cardInfo;
            private CategoryDto.PurchaseCategoryInfo categoryInfo;
        }
    }


    @Data
    public static class RequestAddPurchaseForm {
        private Long accountBookNo;
        private Long cardNo;
        private Long categoryNo;
        private String storeName;
        private PurchaseEntity.PurchaseType purchaseType;
        private int price;
        private String reason;
        private LocalDate purchaseDate;

    }

    public PurchaseEntity toEntity(){
        PurchaseEntity purchaseEntity = PurchaseEntity.builder()
                .storeName(storeName)
                .purchaseType(purchaseType)
                .price(price)
                .reason(reason)
                .purchaseDate(purchaseDate)
                .accountBookInfo(accountBookInfo)
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
