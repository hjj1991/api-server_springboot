package com.hjj.apiserver.domain.category

import com.hjj.apiserver.adapter.out.persistence.BaseEntity
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.purchase.Purchase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@Table(
    name = "tb_category",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["accountBookNo", "categoryName"]),
    ],
)
class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryNo: Long? = null,
    categoryName: String,
    categoryDesc: String = "",
    categoryIcon: String,
    purchaseList: MutableList<Purchase> = mutableListOf(),
    accountBook: AccountBook,
    parentCategory: Category? = null,
    childCategories: MutableList<Category> = mutableListOf(),
) : BaseEntity() {
    @Column(nullable = false)
    var categoryName: String = categoryName
        protected set

    @Column(columnDefinition = "varchar(5000) default ''")
    var categoryDesc: String = categoryDesc
        protected set

    @Column
    var categoryIcon: String = categoryIcon
        protected set

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "category")
    var purchasesList: MutableList<Purchase> = purchaseList
        protected set

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountBookNo")
    var accountBook: AccountBook = accountBook
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCategoryNo", nullable = true)
    var parentCategory: Category? = parentCategory
        protected set

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory", cascade = [CascadeType.ALL])
    var childCategories: MutableList<Category> = childCategories
        protected set

    fun updateCategory(
        categoryName: String,
        categoryDesc: String,
        categoryIcon: String,
        parentCategory: Category?,
    ): Category {
        this.categoryName = categoryName
        this.categoryDesc = categoryDesc
        this.categoryIcon = categoryIcon

        parentCategory?.also {
            changeParentCategory(parentCategory)
        }

        return this
    }

    // 연관관계 편의 메소드
    fun changeParentCategory(parentCategory: Category) {
        this.parentCategory?.also {
            it.childCategories.remove(this)
        }

        this.parentCategory = parentCategory
        parentCategory.childCategories.add(this)
    }
}
