package com.hjj.apiserver.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "tb_account_book_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AccountBookUserEntityJava extends BaseEntityJava {

    public enum AccountRole{
        OWNER, MEMBER, GUEST;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountBookUserNo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accountBookNo")
    private AccountBookEntityJava accountBookEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userNo")
    private UserEntity userEntity;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @Column(length = 10)
    private String backGroundColor;

    @Column(length = 10)
    private String color;

    /* 연관관계 편의 메소드 */
    public void changeAccountBookEntity(AccountBookEntityJava accountBookEntity){
        if(this.accountBookEntity != null){
            this.accountBookEntity.getAccountBookUserEntityList().remove(this);
        }
        this.accountBookEntity = accountBookEntity;
        accountBookEntity.getAccountBookUserEntityList().add(this);
    }

    public void changeUserEntity(UserEntity userEntity){
        if(this.userEntity != null){
            this.userEntity.getAccountBookUserEntityList().remove(this);
        }
        this.userEntity = userEntity;
        userEntity.getAccountBookUserEntityList().add(this);
    }
}
