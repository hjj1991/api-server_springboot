package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.request.CategoryRemoveRequest
import com.hjj.apiserver.dto.category.response.CategoryAddResponse
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.CategoryService
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CategoryController(
    private val categoryService: CategoryService,
) {

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    fun categoryAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestBody @Valid categoryAddRequest: CategoryAddRequest
    ): CategoryAddResponse {
        return categoryService.addCategory(currentUserInfo.userNo, categoryAddRequest)
    }

    @GetMapping("/categories")
    fun categoriesFind(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestParam accountBookNo: Long
    ): CategoryFindAllResponse {
        return categoryService.findAllCategories(currentUserInfo.userNo, accountBookNo)
    }

    @GetMapping("/categories/{categoryNo}")
    fun categoryDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("categoryNo") categoryNo: Long
    ): CategoryDetailResponse {
        return categoryService.findCategory(categoryNo, currentUserInfo.userNo)
    }

    @PatchMapping("/categories/{categoryNo}")
    fun categoryModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("categoryNo") categoryNo: Long,
        @Valid @RequestBody request: CategoryModifyRequest
    ) {
        categoryService.modifyCategory(currentUserInfo.userNo, categoryNo, request)
    }


    @DeleteMapping("/category/{categoryNo}")
    fun categoryRemove(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") categoryNo: Long,
        @RequestBody request: CategoryRemoveRequest
    ) {
        categoryService.deleteCategory(
            categoryNo,
            request.accountBookNo,
            currentUserInfo.userNo
        )
    }

}