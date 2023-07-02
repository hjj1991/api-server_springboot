package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.user.User
import jakarta.persistence.*

@Entity
@Table(
    name = "tb_account_book_user",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["accountBookNo", "userNo"])
    ]
)
class AccountBookUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var accountBookUserNo: Long? = null,
    accountBook: AccountBook,
    user: User,
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
    var user: User = user
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