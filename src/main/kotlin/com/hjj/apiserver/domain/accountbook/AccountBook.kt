package com.hjj.apiserver.domain.accountbook

import com.hjj.apiserver.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@Table(name = "tb_account_book")
class AccountBook(
    accountBookNo: Long? = null,
    accountBookName: String,
    accountBookDesc: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val accountBookNo: Long? = accountBookNo

    @Column(length = 100, nullable = false)
    var accountBookName: String = accountBookName
        protected set

    @Column
    var accountBookDesc: String = accountBookDesc
        protected set
}