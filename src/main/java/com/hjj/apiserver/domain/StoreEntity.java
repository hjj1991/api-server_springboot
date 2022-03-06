package com.hjj.apiserver.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_store")
@Getter
public class StoreEntity  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeNo;

    @Column
    private String storeTypeName;

    @Column(columnDefinition = "varchar(5000) default ''")
    private String storeDesc;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storeInfo")
    private List<PurchaseEntity> purchaseEntityList;
}
