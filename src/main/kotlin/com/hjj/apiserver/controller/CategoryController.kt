package com.hjj.apiserver.controller

import com.hjj.apiserver.common.ApiResponse
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.request.CategoryRemoveRequest
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.CategoryService
import com.hjj.apiserver.util.ApiUtils
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(tags = ["4. Category"])
@RestController
class CategoryController(
    private val categoryService: CategoryService,
) {

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "카테고리 등록", notes = "카테고리를 등록한다.")
    @PostMapping("/category")
    fun categoryAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestBody request: CategoryAddRequest
    ): ApiResponse<*> {
        return ApiUtils.success(categoryService.addCategory(currentUserInfo.userNo, request))
    }


    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "카테고리 리스트를 반환", notes = "카테고리를 리스트를 반환한다.")
    @GetMapping("/category")
    fun categoriesFind(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestParam accountBookNo: Long
    ): ApiResponse<*> {
        return ApiUtils.success(categoryService.findAllCategories(currentUserInfo.userNo, accountBookNo))
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "카테고리 상세 조회", notes = "카테고리를 상세 조회한다.")
    @GetMapping("/category/{categoryNo}")
    fun categoryDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") categoryNo: Long
    ): ApiResponse<*> {
        return ApiUtils.success(categoryService.findCategory(categoryNo))
    }

    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "카테고리를 변경", notes = "카테고리를 변경한다.")
    @PatchMapping("/category/{categoryNo}")
    fun categoryModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") categoryNo: Long,
        @Valid @RequestBody request: CategoryModifyRequest
    ): ApiResponse<*> {
        return ApiUtils.success(categoryService.modifyCategory(currentUserInfo.userNo, categoryNo, request))
    }


    @ApiImplicitParams(
        ApiImplicitParam(
            name = "Authorization",
            value = "로그인 성공 후 access_token",
            required = true,
            dataType = "String",
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @ApiOperation(value = "카테고리를 삭제", notes = "카테고리를 삭제한다.")
    @DeleteMapping("/category/{categoryNo}")
    fun categoryRemove(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") categoryNo: Long,
        @RequestBody request: CategoryRemoveRequest
    ): ApiResponse<*> {
        return ApiUtils.success(
            categoryService.deleteCategory(
                categoryNo,
                request.accountBookNo,
                currentUserInfo.userNo
            )
        )
    }

}