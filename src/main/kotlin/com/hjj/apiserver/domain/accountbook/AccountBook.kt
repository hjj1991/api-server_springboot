package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
@Table(name = "tb_account_book")
class AccountBook(
    accountBookName: String,
    accountBookDesc: String,
    accountBookUserList: MutableList<AccountBookUser> = mutableListOf(),
    purchaseList: MutableList<Purchase> = mutableListOf(),
    categories: MutableList<Category> = mutableListOf(),
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val accountBookNo: Long? = null

    @Column(length = 100, nullable = false)
    var accountBookName: String = accountBookName
        protected set

    @Column
    var accountBookDesc: String = accountBookDesc
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBook")
    var accountBookUserList: MutableList<AccountBookUser> = accountBookUserList
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBook")
    @BatchSize(size = 100)
    var purchaseList: MutableList<Purchase> = purchaseList
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountBook")
    @BatchSize(size = 100)
    var categories: MutableList<Category> = categories
        protected set
}