package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.CategoryDto;
import com.hjj.apiserver.service.CategoryService;
import com.hjj.apiserver.util.CurrentUser;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"4. Category"})
@Slf4j
@RestController
@AllArgsConstructor
public class CategoryController {
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "카테고리 등록", notes = "카테고리를 등록한다.")
    @PostMapping("/category")
    public ApiResponse categoryAdd(@CurrentUser UserEntity user, @RequestBody CategoryDto.RequestCategoryAddForm form) {
        try {
            CategoryDto categoryDto = modelMapper.map(form, CategoryDto.class);
            categoryDto.setUserNo(user.getUserNo());
            categoryService.addCategory(categoryDto);

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("categoryAdd Error userNo: {}, form: {}, exception: {}", user.getUserNo(), form, e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "카테고리 리스트를 반환", notes = "카테고리를 리스트를 반환한다.")
    @GetMapping("/category")
    public ApiResponse categoryList(@CurrentUser UserEntity user, @RequestParam(value = "accountBookNo") Long accountBookNo) {
        try {
            return ApiUtils.success(categoryService.findAllCategory(user.getUserNo(), accountBookNo));
        } catch (Exception e) {
            log.error("categoryList Error userNo: {}, accountBookNo: [}, exception: {}", user.getUserNo(), accountBookNo, e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "카테고리 상세 조회", notes = "카테고리를 상세 조회한다.")
    @GetMapping("/category/{categoryNo}")
    public ApiResponse categoryDetails(@CurrentUser UserEntity user, @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") Long categoryNo) {
        try {

            return ApiUtils.success(categoryService.findCategory(categoryNo));
        } catch (Exception e) {
            log.error("categoryModify Error userNo: {}, categoryNo: {}, exception: {}", user.getUserNo(), categoryNo, e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "카테고리를 변경", notes = "카테고리를 변경한다.")
    @PatchMapping("/category/{categoryNo}")
    public ApiResponse categoryModify(@CurrentUser UserEntity user, @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") Long categoryNo, @Valid @RequestBody CategoryDto.RequestCategoryModifyForm form) {
        try {
            CategoryDto categoryDto = modelMapper.map(form, CategoryDto.class);
            categoryDto.setUserNo(user.getUserNo());
            categoryDto.setCategoryNo(categoryNo);

            categoryService.modifyCategory(categoryDto);

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("categoryModify Error userNo: {}, form: {}, exception: {}", user.getUserNo(), form, e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "카테고리를 삭제", notes = "카테고리를 삭제한다.")
    @DeleteMapping("/category/{categoryNo}")
    public ApiResponse categoryRemove(@CurrentUser UserEntity user, @ApiParam(value = "categoryNo", required = true) @PathVariable("categoryNo") Long categoryNo, @RequestBody CategoryDto.RequestCategoryRemoveForm form) {
        try {
            categoryService.deleteCategory(categoryNo, form.getAccountBookNo(), user.getUserNo());

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("categoryDelete Error userNo: {}, categoryNo: {}, exception: {}", user.getUserNo(), categoryNo, e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

}
