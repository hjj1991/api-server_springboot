package com.hjj.apiserver.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tb_account_book")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AccountBookEntityJava extends BaseEntityJava {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountBookNo;

    @Column(length = 100, nullable = false)
    private String accountBookName;

    @Column
    private String accountBookDesc;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBookEntity")
    private List<AccountBookUserEntityJava> accountBookUserEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBookEntity")
    @BatchSize(size = 100)
    private List<PurchaseEntityJava> purchaseEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBookEntity")
    private List<CategoryEntityJava> categoryEntityList = new ArrayList<>();

}
