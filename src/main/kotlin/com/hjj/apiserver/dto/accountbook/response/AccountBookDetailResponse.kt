package com.hjj.apiserver.dto.accountbook.response

class AccountBookDetailResponse(
    var accountBookName: String,
    var categories: List<CategoryDetail>,
    var cards: List<CardDetail> = listOf(),
) {

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
        var childCategories: List<CategoryDetail> = mutableListOf()
    )
}