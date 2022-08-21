package com.hjj.apiserver.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "tb_card")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class CardEntityJava extends BaseEntityJava {

    public enum CardType {
        CHECK_CARD,
        CREDIT_CARD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardNo;

    @Column(nullable = false, length = 100)
    private String cardName;

    @Column
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(columnDefinition = "varchar(5000) default ''")
    private String cardDesc;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo", nullable = false)
    private UserEntity userEntity;

    public CardEntityJava update(String cardName, CardType cardType, String cardDesc){
        this.cardName = cardName;
        this.cardType = cardType;
        this.cardDesc = cardDesc;

        return this;
    }

    public void delete(){
        setDeleteYn('Y');
    }


}
