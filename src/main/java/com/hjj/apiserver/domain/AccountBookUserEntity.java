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
public class AccountBookUserEntity extends BaseEntity{

    public enum AccountRole{
        OWNER, MEMBER, GUEST;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountBookUserNo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accountBookNo")
    private AccountBookEntity accountBookInfo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userNo")
    private UserEntity userInfo;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;

    @Column(length = 10)
    private String backGroundColor;

    @Column(length = 10)
    private String color;

    /* 연관관계 편의 메소드 */
    public void changeAccountBookInfo(AccountBookEntity accountBookInfo){
        if(this.accountBookInfo != null){
            this.accountBookInfo.getAccountBookUserEntityList().remove(this);
        }
        this.accountBookInfo = accountBookInfo;
        accountBookInfo.getAccountBookUserEntityList().add(this);
    }

    public void changeUserInfo(UserEntity userInfo){
        if(this.userInfo != null){
            this.userInfo.getAccountBookUserEntityList().remove(this);
        }
        this.userInfo = userInfo;
        userInfo.getAccountBookUserEntityList().add(this);
    }
}
