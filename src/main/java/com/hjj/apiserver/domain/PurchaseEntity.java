package com.hjj.apiserver.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_purchase")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @JoinColumn(name="cardEntity_cardNo", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private CardEntity cardInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="storeEntity_storeNo", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private StoreEntity storeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userEntity_userNo", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private UserEntity userInfo;


    public void changeStoreInfo(StoreEntity storeInfo){
        if(this.storeInfo != null){
            this.storeInfo.getPurchaseEntityList().remove(storeInfo);
        }
        this.storeInfo = storeInfo;
        storeInfo.getPurchaseEntityList().add(this);
    }

    public void delete(){
        setDeleteYn('Y');
    }


}
