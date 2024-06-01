package com.hjj.apiserver.domain.purchase

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
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
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate

@Entity
@DynamicUpdate
@Table(name = "tb_purchase")
class Purchase(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val purchaseNo: Long? = null,
    purchaseType: PurchaseType,
    price: Int,
    reason: String,
    purchaseDate: LocalDate,
    card: Card? = null,
    category: Category? = null,
    userEntity: UserEntity,
    accountBook: AccountBook,
) : BaseEntity() {
    @Column
    @Enumerated(EnumType.STRING)
    var purchaseType: PurchaseType = purchaseType
        protected set

    @Column
    var price: Int = price
        protected set

    @Column(columnDefinition = "varchar(5000) default ''")
    var reason: String = reason
        protected set

    @Column
    var purchaseDate: LocalDate = purchaseDate
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardNo", nullable = true)
    var card: Card? = card
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryNo")
    var category: Category? = category
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", nullable = false)
    var userEntity: UserEntity = userEntity
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountBookNo", nullable = false)
    var accountBook: AccountBook = accountBook
        protected set

    // 연관관계 편의 메서드
    fun changeCategory(category: Category) {
        this.category?.also {
            it.purchasesList.remove(this)
        }

        this.category = category
        category.purchasesList.add(this)
    }

    // 연관관계 편의 메서드
    fun changeUser(userEntity: UserEntity) {
        this.userEntity.purchaseList.remove(this)

        this.userEntity = userEntity
        userEntity.purchaseList.add(this)
    }

    fun updatePurchase(
        request: PurchaseModifyRequest,
        card: Card?,
        category: Category?,
    ): Purchase {
        var updateCard = card
        var updateCategory = category
        if (request.purchaseType == PurchaseType.INCOME) {
            updateCard = null
            updateCategory = null
        }

        this.category = updateCategory
        this.card = updateCard
        this.purchaseType = request.purchaseType
        this.price = request.price
        this.reason = request.reason
        this.purchaseDate = request.purchaseDate

        return this
    }
}
