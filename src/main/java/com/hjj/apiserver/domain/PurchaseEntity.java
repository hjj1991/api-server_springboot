package com.hjj.apiserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_purchase")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PurchaseEntity extends BaseEntity {

    public enum PurchaseType {
        INCOME,
        OUTGOING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseNo;

    @Column(columnDefinition = "varchar(500) default ''", nullable = true)
    private String storeName;

    @Column
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;

    @Column
    private int price;

    @Column(columnDefinition = "varchar(5000) default ''", nullable = true)
    private String reason;

    @Column(columnDefinition = "char(1) default 'N'", nullable = false)
    private char refundYn;

    @Column
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cardEntity_cardNo", nullable = true)
    private CardEntity cardInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="storeEntity_storeNo", nullable = true)
    private StoreEntity storeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userEntity_userNo", nullable = false)
    private UserEntity userInfo;


    public void delete(){
        setDeleteYn('Y');
        setDeleteDate(LocalDateTime.now());
    }


}
