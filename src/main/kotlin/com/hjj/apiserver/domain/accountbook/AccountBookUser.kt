package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.user.User
import org.hibernate.annotations.DynamicUpdate
import jakarta.persistence.*

@Entity
@DynamicUpdate
@Table(name = "tb_account_book_user")
class AccountBookUser(
    accountBook: AccountBook,
    user: User,
    accountRole: AccountRole,
    backGroundColor: String,
    color: String,
): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val accountBookUserNo: Long? = null

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


    /* 연관관계 편의 메서드 */
    fun changeAccountBook(accountBook: AccountBook) {
        this.accountBook = accountBook
        accountBook.accountBookUserList.add(this)
    }

    /* 연관관계 편의 메서드 */
    fun changeUser(user: User) {
        this.user = user
        user.accountBookUserList.add(this)
    }
}