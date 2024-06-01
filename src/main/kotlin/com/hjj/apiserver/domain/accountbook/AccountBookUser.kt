package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "tb_account_book_user",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["accountBookNo", "userNo"]),
    ],
)
class AccountBookUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var accountBookUserNo: Long? = null,
    accountBook: AccountBook,
    userEntity: UserEntity,
    accountRole: AccountRole,
    backGroundColor: String,
    color: String,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "accountBookNo")
    var accountBook: AccountBook = accountBook
        protected set

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "userNo")
    var userEntity: UserEntity = userEntity
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var accountRole: AccountRole = accountRole
        protected set

    @Column(length = 10)
    var backGroundColor: String = backGroundColor
        protected set

    @Column(length = 10)
    var color: String = color
        protected set
}
