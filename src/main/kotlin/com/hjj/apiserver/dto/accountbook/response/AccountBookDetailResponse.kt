package com.hjj.apiserver.dto.accountbook.response

class AccountBookDetailResponse(
    var accountBookName: String,
    var categories: List<CategoryDetail> = listOf(),
) {

    constructor(
        accountBookName: String,
        categories: List<CategoryDetail> = listOf(),
        cards: List<CardDetail> = listOf(),
    ) : this(accountBookName, categories) {
        this.cards = cards
    }
    var cards: List<CardDetail> = listOf()

    class CardDetail(
        var cardNo: Long,
        var cardName: String,
    )

    class CategoryDetail(
        var categoryNo: Long,
        var categoryName: String,
        var categoryIcon: String,
        var accountBookNo: Long,
        var accountBookName: String,
        var childCategories: List<ChildrenCategory> = mutableListOf()
    )

    class ChildrenCategory(
        var categoryNo: Long,
        var categoryName: String,
        var categoryIcon: String,
        var accountBookNo: Long,
        var accountBookName: String,
        var parentCategoryNo: Long,
    )
}