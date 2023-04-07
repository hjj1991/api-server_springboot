package com.hjj.apiserver.domain.card

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.user.User
import org.hibernate.annotations.DynamicUpdate
import jakarta.persistence.*

@Entity
@DynamicUpdate
@Table(name = "tb_card")
class Card(
    cardNo: Long? = null,
    cardName: String,
    cardType: CardType,
    cardDesc: String = "",
    user: User,
): BaseEntity(){


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardNo: Long? = cardNo

    @Column(nullable = false, length = 100)
    var cardName: String = cardName
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var cardType: CardType = cardType
        protected set

    @Column(columnDefinition = "varchar(5000) default ''")
    var cardDesc: String = cardDesc
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", nullable = false)
    var user: User = user
        protected set

    fun updateCard(cardName: String, cardType: CardType, cardDesc: String){
        this.cardName = cardName
        this.cardType = cardType
        this.cardDesc = cardDesc
    }

    fun delete() {
        this.deleteYn = 'Y'
    }
}