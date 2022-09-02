package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addBasicCategory(accountBook: AccountBook){
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
        for(i: Int in categoryName.indices){
            categories.add(Category(
                categoryName = categoryName[i],
                categoryDesc = categoryDesc[i],
                categoryIcon = baseIconUrl + categoryIcon[i],
                accountBook = accountBook,
            ))
        }

        categoryRepository.saveAll(categories)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addCategory(userNo: Long, request: CategoryAddRequest){
        val accountBook = accountBookRepository.findAccountBookBySubQuery(
            userNo = userNo,
            accountBookNo = request.accountBookNo,
            AccountRole.OWNER
        )?: throw IllegalArgumentException()

        categoryRepository.save(Category(
            categoryName = request.categoryName,
            categoryDesc = request.categoryDesc,
            categoryIcon = request.categoryIcon,
            accountBook = accountBook,
            parentCategory = request.parentCategoryNo?.let { categoryRepository.getById(it) }
        ))
    }

    fun findAllCategories(userNo: Long, accountBookNo: Long): List<CategoryFindAllResponse> {
        return categoryRepository.findCategories(userNo, accountBookNo)

    }

    fun findCategory(categoryNo: Long): CategoryDetailResponse{
        return categoryRepository.findByIdOrNull(categoryNo)?.run { CategoryDetailResponse(
            accountBookNo = accountBook.accountBookNo!!,
            categoryNo = categoryNo,
            categoryName = categoryName,
            categoryDesc = categoryDesc,
            categoryIcon = categoryIcon,
            childCategories = childCategories.map {
                CategoryDetailResponse.ChildCategory(
                    accountBookNo = it.accountBook.accountBookNo!!,
                    categoryNo = it.categoryNo!!,
                    parentCategoryNo = it.parentCategory!!.categoryNo!!,
                    categoryName = it.categoryName,
                    categoryDesc = it.categoryDesc,
                    categoryIcon = it.categoryIcon,
                    createdDate = it.createdDate,
                    lastModifiedDate = it.lastModifiedDate
            ) }
        ) }?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false)
    fun modifyCategory(userNo: Long, categoryNo: Long, request: CategoryModifyRequest){
        categoryRepository.findCategoryByOwner(categoryNo, request.accountBookNo, userNo)?.also {
            /* 최상위 카테고리의 경우 자식카테고리가 될 수 없다. 자기자신을 부모 카테고리로 설정 할시 에러 */
            if((it.parentCategory == null && request.parentCategoryNo != null)
                || it.categoryNo == request.parentCategoryNo){
                throw IllegalArgumentException()
            }

            it.updateCategory(
                categoryName = request.categoryName,
                categoryDesc = request.categoryDesc,
                categoryIcon = request.categoryIcon,
                parentCategory = request.parentCategoryNo?.let { categoryRepository.getById(request.parentCategoryNo) },
            )
        }
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun deleteCategory(categoryNo: Long, accountBookNo: Long, userNo: Long){
        categoryRepository.delete(categoryRepository.findCategoryByOwner(categoryNo, accountBookNo, userNo))
    }
}