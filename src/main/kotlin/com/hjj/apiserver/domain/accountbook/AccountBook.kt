package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicUpdate
import jakarta.persistence.*

@Entity
@DynamicUpdate
@Table(name = "tb_account_book")
class AccountBook(
    accountBookName: String,
    accountBookDesc: String,
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
}