package com.hjj.apiserver.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntity  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeNo;

    @Column
    private String storeTypeName;

    @Column(columnDefinition = "varchar(5000) default ''")
    private String storeDesc;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storeInfo")
    @Builder.Default
    private List<PurchaseEntity> purchaseEntityList = new ArrayList<>();
}
