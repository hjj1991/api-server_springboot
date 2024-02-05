package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.BaseTimeEntity
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.user.Role
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "tb_user",
    indexes = [
        Index(name = "idx_nick_name", columnList = "nick_name"),
        Index(name = "user_email", columnList = "user_email")
    ],
)
class UserEntity(
    userNo: Long = 0L,
    nickName: String,
    userEmail: String? = null,
    userPw: String? = null,
    picture: String? = null,
    purchaseList: MutableList<Purchase> = mutableListOf(),
    cards: MutableList<Card> = mutableListOf(),
    role: Role = Role.USER,
) : BaseTimeEntity() {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userNo: Long = userNo

    @Column(length = 20, unique = true)
    var nickName: String = nickName

    @Column(length = 200)
    var userEmail: String? = userEmail

    @Column(length = 300)
    var userPw: String? = userPw

    @Column
    var picture: String? = picture

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "userEntity", fetch = FetchType.LAZY)
    val purchaseList: MutableList<Purchase> = purchaseList

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "userEntity", fetch = FetchType.LAZY)
    val cards: MutableList<Card> = cards

    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    var deleteYn: Char = 'N'

    @Column(nullable = false, columnDefinition = "varchar(20) default 'USER'")
    @Enumerated(EnumType.STRING)
    var role: Role = role

}