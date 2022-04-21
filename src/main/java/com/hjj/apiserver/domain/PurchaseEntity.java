package com.hjj.apiserver.domain;

import com.hjj.apiserver.dto.PurchaseDto;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

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
    private CardEntity cardInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryNo")
    private CategoryEntity categoryInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo", nullable = false)
    private UserEntity userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="accountBookNo", nullable = false)
    private AccountBookEntity accountBookInfo;


    public void changeCategoryInfo(CategoryEntity categoryInfo){
        if(this.categoryInfo != null){
            this.categoryInfo.getPurchaseEntityList().remove(this);
        }
        this.categoryInfo = categoryInfo;
        categoryInfo.getPurchaseEntityList().add(this);
    }

    public void changeUserInfo(UserEntity userInfo){
        if(this.userInfo != null){
            this.userInfo.getPurchaseEntityList().remove(this);
        }
        this.userInfo = userInfo;
        userInfo.getPurchaseEntityList().add(this);
    }

    public void changeAccountBookInfo(AccountBookEntity accountBookInfo){
        if(this.accountBookInfo != null){
            this.accountBookInfo.getPurchaseEntityList().remove(accountBookInfo);
        }
        this.accountBookInfo = accountBookInfo;
        accountBookInfo.getPurchaseEntityList().add(this);
    }


    public PurchaseEntity updatePurchase(PurchaseDto purchaseDto){
        if(purchaseDto.getCardInfo() != null){
            this.cardInfo = purchaseDto.getCardInfo();
        }
        if(purchaseDto.getCategoryInfo() != null){
            changeCategoryInfo(purchaseDto.getCategoryInfo());
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
