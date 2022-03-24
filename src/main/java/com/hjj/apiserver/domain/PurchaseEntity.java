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
    @JoinColumn(name="categoryEntity_categoryNo")
    private CategoryEntity categoryInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userEntity_userNo", nullable = false)
    private UserEntity userInfo;


    public void changeCategoryInfo(CategoryEntity categoryInfo){
        if(this.categoryInfo != null){
            this.categoryInfo.getPurchaseEntityList().remove(categoryInfo);
        }
        this.categoryInfo = categoryInfo;
        categoryInfo.getPurchaseEntityList().add(this);
    }

    public void delete(){
        setDeleteYn('Y');
    }


}
