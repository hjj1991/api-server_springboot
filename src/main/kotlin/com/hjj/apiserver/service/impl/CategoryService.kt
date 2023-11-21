package com.hjj.apiserver.service.impl

import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.exception.AccountBookAccessDeniedException
import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.CategoryNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.response.CategoryAddResponse
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
    private val accountBookUserRepository: AccountBookUserRepository,
) {

    @Transactional(readOnly = false)
    fun addCategory(userNo: Long, request: CategoryAddRequest): CategoryAddResponse {
        val accountBook = accountBookRepository.findAccountBook(
            userNo = userNo,
            accountBookNo = request.accountBookNo,
        ) ?: throw AccountBookNotFoundException()
        val parentCategory = getParentCategory(request.parentCategoryNo, accountBook.accountBookNo)
        val savedCategory = categoryRepository.save(
            request.toEntity(
                accountBookRepository.getReferenceById(accountBook.accountBookNo),
                parentCategory
            )
        )
        return CategoryAddResponse.of(savedCategory)
    }

    fun findAllCategories(userNo: Long, accountBookNo: Long): CategoryFindAllResponse {
        val findAccountRole = accountBookUserRepository.findAccountRole(userNo, accountBookNo)
        validateAccountBookRole(findAccountRole)
        val findCategories = categoryRepository.findCategories(userNo, accountBookNo)
        return CategoryFindAllResponse.of(findCategories, findAccountRole)
    }

    fun findCategory(userNo: Long, categoryNo: Long): CategoryDetailResponse {
        val findCategory = categoryRepository.findCategoryByCategoryNo(userNo, categoryNo)
            ?: throw CategoryNotFoundException()
        val findAccountRole = accountBookUserRepository.findAccountRole(userNo, findCategory.accountBook.accountBookNo!!)
        validateAccountBookRole(findAccountRole)
        return CategoryDetailResponse.of(findCategory)
    }

    @Transactional(readOnly = false)
    fun modifyCategory(userNo: Long, categoryNo: Long, request: CategoryModifyRequest): Category {
        val category = categoryRepository.findCategoryByAccountRole(
            categoryNo,
            request.accountBookNo,
            userNo,
            setOf(AccountRole.OWNER)
        ) ?: throw CategoryNotFoundException()

        modifyValidate(category, request)
        val parentCategory = getParentCategory(request.parentCategoryNo, request.accountBookNo)

        return category.updateCategory(
            categoryName = request.categoryName,
            categoryDesc = request.categoryDesc,
            categoryIcon = request.categoryIcon,
            parentCategory = parentCategory,
        )
    }

    @Transactional(readOnly = false)
    fun deleteCategory(categoryNo: Long, accountBookNo: Long, userNo: Long) {
        val category = categoryRepository.findCategoryByAccountRole(
            categoryNo,
            accountBookNo,
            userNo,
            setOf(AccountRole.OWNER)
        ) ?: throw CategoryNotFoundException()

        category.delete()
    }

    private fun getParentCategory(parentCategoryNo: Long?, accountBookNo: Long): Category? {
        if (parentCategoryNo == null) {
            return null
        }

        return categoryRepository.findCategoryByCategoryNoAndAccountBook_AccountBookNoAndIsDeleteIsFalse(
            parentCategoryNo,
            accountBookNo
        ) ?: throw CategoryNotFoundException(ErrCode.ERR_CODE0011.msg)
    }

    private fun modifyValidate(category: Category, request: CategoryModifyRequest) {
        /* 최상위 카테고리의 경우 자식카테고리가 될 수 없다. 자기자신을 부모 카테고리로 설정 할시 에러 */
        if ((category.parentCategory == null && request.parentCategoryNo != null) || category.categoryNo == request.parentCategoryNo) {
            throw IllegalArgumentException()
        }
    }

    private fun validateAccountBookRole(accountRole: AccountRole?) {
        if(accountRole == null || !accountRole.hasReadPermission())
            throw AccountBookAccessDeniedException()
    }
}