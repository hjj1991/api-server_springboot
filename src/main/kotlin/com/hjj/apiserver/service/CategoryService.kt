package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
    private val accountBookUserRepository: AccountBookUserRepository,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addCategory(userNo: Long, request: CategoryAddRequest) {
        val accountBook = accountBookRepository.findAccountBook(
            userNo = userNo,
            accountBookNo = request.accountBookNo,
        ) ?: throw IllegalArgumentException()

//        categoryRepository.save(Category(
//            categoryName = request.categoryName,
//            categoryDesc = request.categoryDesc,
//            categoryIcon = request.categoryIcon,
//            accountBook = accountBook,
//            parentCategory = request.parentCategoryNo?.let { categoryRepository.getById(it) }
//        ))
    }

    fun findAllCategories(userNo: Long, accountBookNo: Long): CategoryFindAllResponse {
        return CategoryFindAllResponse(
            categories = categoryRepository.findCategories(userNo, accountBookNo),
            accountRole = accountBookUserRepository.findAccountRole(userNo, accountBookNo)
        )
    }

    fun findCategory(categoryNo: Long): CategoryDetailResponse {
        return categoryRepository.findByIdOrNull(categoryNo)?.run {
            CategoryDetailResponse(
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
                        createdDate = it.createdAt,
                        lastModifiedDate = it.modifiedAt
                    )
                }
            )
        } ?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false)
    fun modifyCategory(userNo: Long, categoryNo: Long, request: CategoryModifyRequest) {
        categoryRepository.findCategoryByAccountRole(
            categoryNo,
            request.accountBookNo,
            userNo,
            setOf(AccountRole.OWNER)
        )?.also {
            /* 최상위 카테고리의 경우 자식카테고리가 될 수 없다. 자기자신을 부모 카테고리로 설정 할시 에러 */
            if ((it.parentCategory == null && request.parentCategoryNo != null)
                || it.categoryNo == request.parentCategoryNo
            ) {
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
    fun deleteCategory(categoryNo: Long, accountBookNo: Long, userNo: Long) {

        val category = categoryRepository.findCategoryByAccountRole(
            categoryNo,
            accountBookNo,
            userNo,
            setOf(AccountRole.OWNER)
        ) ?: throw NoSuchElementException()

        categoryRepository.delete(category)
    }
}