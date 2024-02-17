package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.request.CategoryRemoveRequest
import com.hjj.apiserver.dto.category.response.CategoryAddResponse
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.impl.CategoryService
import com.hjj.apiserver.util.AuthUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    private val categoryService: CategoryService,
) {
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    fun categoryAdd(
        @AuthUser authUserInfo: CurrentUserInfo,
        @RequestBody @Valid categoryAddRequest: CategoryAddRequest,
    ): CategoryAddResponse {
        return categoryService.addCategory(authUserInfo.userNo, categoryAddRequest)
    }

    @GetMapping("/categories")
    fun categoriesFind(
        @AuthUser authUserInfo: CurrentUserInfo,
        @RequestParam accountBookNo: Long,
    ): CategoryFindAllResponse {
        return categoryService.findAllCategories(authUserInfo.userNo, accountBookNo)
    }

    @GetMapping("/categories/{categoryNo}")
    fun categoryDetail(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("categoryNo") categoryNo: Long,
    ): CategoryDetailResponse {
        return categoryService.findCategory(categoryNo, authUserInfo.userNo)
    }

    @PatchMapping("/categories/{categoryNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun categoryModify(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("categoryNo") categoryNo: Long,
        @Valid @RequestBody request: CategoryModifyRequest,
    ) {
        categoryService.modifyCategory(authUserInfo.userNo, categoryNo, request)
    }

    @DeleteMapping("/categories/{categoryNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun categoryRemove(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("categoryNo") categoryNo: Long,
        @RequestBody request: CategoryRemoveRequest,
    ) {
        categoryService.deleteCategory(
            categoryNo,
            request.accountBookNo,
            authUserInfo.userNo,
        )
    }
}
