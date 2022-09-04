package com.hjj.apiserver.domain.purchase

import com.hjj.apiserver.domain.BaseEntity
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import javax.persistence.*

@Entity
@DynamicUpdate
@Table(name = "tb_purchase")
class Purchase(
    purchaseType: PurchaseType,
    price: Int,
    reason: String,
    purchaseDate: LocalDate,
    card: Card? = null,
    category: Category? = null,
    user: User,
    accountBook: AccountBook,
): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val purchaseNo: Long? = null

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
    @JoinColumn(name="cardNo", nullable = true)
    var card: Card? = card
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryNo")
    var category: Category? = category
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountBookNo", nullable = false)
    var accountBook: AccountBook = accountBook
        protected set

    /* 연관관계 편의 메서드 */
    fun changeCategory(category: Category){
        this.category?.also {
            it.purchasesList.remove(this)
        }

        this.category = category
        category.purchasesList.add(this)
    }

    /* 연관관계 편의 메서드 */
    fun changeUser(user: User) {
        this.user.purchaseList.remove(this)

        this.user = user
        user.purchaseList.add(this)
    }

    /* 연관관계 편의 메서드 */
    fun changeAccountBook(accountBook: AccountBook){
        this.accountBook.purchaseList.remove(this)

        this.accountBook = accountBook
        accountBook.purchaseList.add(this)
    }

    fun updatePurchase(request: PurchaseModifyRequest, card: Card?, category: Category?): Purchase{
        var updateCard = card
        var updateCategory = category
        if(request.purchaseType == PurchaseType.INCOME){
            updateCard = null
            updateCategory = null
        }

        this.category = updateCategory
        this.card = updateCard
        purchaseType = request.purchaseType
        price = request.price
        reason = request.reason
        purchaseDate = request.purchaseDate

        return this
    }

    fun delete() {
        deleteYn = 'Y'
    }

}