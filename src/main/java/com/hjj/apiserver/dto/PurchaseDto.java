package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.StoreEntity;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

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
    private char refundYn;
    private LocalDate purchaseDate;
    private CardEntity cardInfo;
    private StoreEntity storeInfo;
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
        private Long storeNo;
        private String storeName;
        private PurchaseEntity.PurchaseType purchaseType;
        private int price;
        private String reason;
        private char refundYn;
        private LocalDate purchaseDate;
        private CardDto cardInfo;
        private StoreDto storeInfo;

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
                .refundYn('N')
                .purchaseDate(purchaseDate)
                .cardInfo(cardInfo)
                .storeInfo(storeInfo)
                .userInfo(userInfo)
                .build();

        return purchaseEntity;
    }
}
