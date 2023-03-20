package com.hjj.apiserver.util

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.category.Category

class CommonUtils {

    companion object {
        /* 페이징 slice */
        fun <T> getSlicePageResult(result: List<T>, limit: Int): List<T> {
            val returnValue: MutableList<T> = ArrayList()
            var cnt = 0
            for (obj in result) {
                if (cnt == limit) {
                    break
                }
                returnValue.add(obj)
                cnt++
            }
            return returnValue
        }

        fun createBasicCategories(accountBook: AccountBook): List<Category> {
            val categoryName = arrayOf(
                "식비",
                "카페/간식",
                "유흥",
                "생활",
                "쇼핑",
                "교통",
                "통신/주거",
                "금융",
                "의료",
                "문화",
                "여행/숙박",
                "교육",
                "경조",
                "자녀/육아",
                "반려동물"
            )
            val categoryDesc = arrayOf(
                "기본적인 식비",
                "식비를 제외한 카페, 디저트 등등",
                "술, 유흥",
                "생활하는데에 있어서 자잘한 금액",
                "온라인 쇼핑, 오프라인 쇼핑 등등",
                "버스/택시/지하철 요금",
                "인터넷비, 휴대폰비/월세, 전세자금등등",
                "투자금, 주식,코인",
                "병원비, 약국, 수술비",
                "영화, 뮤지컬, 여러 문환생활",
                "여행비, 숙박비 등등",
                "학원비, 온라인 강의",
                "경조사비",
                "육아비",
                "반려동물 케어 비용"
            )
            val baseIconUrl = "/images/"
            val categoryIcon = arrayOf(
                "food.png",
                "coffee.png",
                "beer.png",
                "life.png",
                "shopping.png",
                "bus.png",
                "home.png",
                "money.png",
                "heart.png",
                "culture.png",
                "airplane.png",
                "education.png",
                "congratulation.png",
                "baby.png",
                "pet.png"
            )
            val categories = mutableListOf<Category>()
            for (i: Int in categoryName.indices) {
                categories.add(
                    Category(
                        categoryName = categoryName[i],
                        categoryDesc = categoryDesc[i],
                        categoryIcon = baseIconUrl + categoryIcon[i],
                        accountBook = accountBook,
                    )
                )
            }

            return categories.toList()
        }
    }
}
