package com.hjj.apiserver.domain.card

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@Table(name = "tb_card")
class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardNo: Long? = null,
    cardName: String,
    cardType: CardType,
    cardDesc: String = "",
    userEntity: UserEntity,
) : BaseEntity() {
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
    var userEntity: UserEntity = userEntity
        protected set

    fun updateCard(
        cardName: String,
        cardType: CardType,
        cardDesc: String,
    ) {
        this.cardName = cardName
        this.cardType = cardType
        this.cardDesc = cardDesc
    }
}
