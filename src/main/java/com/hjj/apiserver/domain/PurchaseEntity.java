package com.hjj.apiserver.domain;

import com.hjj.apiserver.dto.PurchaseDto;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_purchase")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
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
    private Integer price;

    @Column(columnDefinition = "varchar(5000) default ''", nullable = true)
    private String reason;

    @Column
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cardNo", nullable = true)
    private CardEntity cardEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryNo")
    private CategoryEntity categoryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="accountBookNo", nullable = false)
    private AccountBookEntity accountBookEntity;


    public void changeCategoryEntity(CategoryEntity categoryEntity){
        if(this.categoryEntity != null){
            this.categoryEntity.getPurchaseEntityList().remove(this);
        }
        this.categoryEntity = categoryEntity;
        categoryEntity.getPurchaseEntityList().add(this);
    }

    public void changeUserEntity(UserEntity userEntity){
        if(this.userEntity != null){
            this.userEntity.getPurchaseEntityList().remove(this);
        }
        this.userEntity = userEntity;
        userEntity.getPurchaseEntityList().add(this);
    }

    public void changeAccountBookEntity(AccountBookEntity accountBookEntity){
        if(this.accountBookEntity != null){
            this.accountBookEntity.getPurchaseEntityList().remove(accountBookEntity);
        }
        this.accountBookEntity = accountBookEntity;
        accountBookEntity.getPurchaseEntityList().add(this);
    }


    public PurchaseEntity updatePurchase(PurchaseDto purchaseDto){
        if(purchaseDto.getCardEntity() != null){
            this.cardEntity = purchaseDto.getCardEntity();
        }
        if(purchaseDto.getCategoryEntity() != null){
            changeCategoryEntity(purchaseDto.getCategoryEntity());
        }
        if(purchaseDto.getStoreName() != null){
            this.storeName = purchaseDto.getStoreName();
        }
        if(purchaseDto.getPurchaseType() != null){
            this.purchaseType = purchaseDto.getPurchaseType();
        }
        if(purchaseDto.getPrice() != null){
            this.price = purchaseDto.getPrice();
        }
        if(purchaseDto.getReason() != null){
            this.reason = purchaseDto.getReason();
        }
        if(purchaseDto.getPurchaseDate() != null){
            this.purchaseDate = purchaseDto.getPurchaseDate();
        }
        return this;
    }

    public void delete(){
        setDeleteYn('Y');
    }


}
