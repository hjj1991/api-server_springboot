package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.repositroy.AccountBookRepository;
import com.hjj.apiserver.service.CardService;
import com.hjj.apiserver.service.CategoryService;
import com.hjj.apiserver.service.PurchaseService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"3. Purchase"})
@RestController
@AllArgsConstructor
@Slf4j
public class PurchaseController {

    private final ModelMapper modelMapper;
    private final PurchaseService purchaseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 등록", notes = "지출, 수입을 등록한다.")
    @PostMapping("/purchase")
    public ApiResponse purchaseAdd(@AuthenticationPrincipal TokenDto user, @RequestBody PurchaseDto.RequestAddPurchaseForm requestAddPurchaseForm) {
        try {
            PurchaseDto purchaseDto = modelMapper.map(requestAddPurchaseForm, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());

            purchaseService.addPurchase(purchaseDto);

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("addPurchase: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 리스트", notes = "지출, 수입을 리스트를 불러온다.")
    @GetMapping("/purchase")
    public ApiResponse purchaseFindList(@AuthenticationPrincipal TokenDto user, PurchaseDto.RequestPurchaseFindForm form) {
        try {
            PageRequest pageRequest = PageRequest.of(form.getPage(), form.getSize());
            PurchaseDto purchaseDto = modelMapper.map(form, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());

            return ApiUtils.success(purchaseService.findPurchaseListOfPage(purchaseDto, pageRequest));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 삭제", notes = "지출, 수입을 삭제한다.")
    @DeleteMapping("/purchase/{purchaseNo}")
    public ApiResponse purchaseDelete(@AuthenticationPrincipal TokenDto user, @PathVariable("purchaseNo") Long purchaseNo) {
        try {

            purchaseService.deletePurchase(user.getUserNo(), purchaseNo);


            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출, 수입 상세조회", notes = "지출, 수입을 상세 조회한다.")
    @GetMapping("/purchase/{purchaseNo}")
    public ApiResponse purchaseDetail(@AuthenticationPrincipal TokenDto user, @PathVariable("purchaseNo") Long purchaseNo) {
        try {
            return ApiUtils.success(purchaseService.findPurchase(user.getUserNo(), purchaseNo));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출, 수입 수정", notes = "지출, 수입을 수정한다.")
    @PatchMapping("/purchase/{purchaseNo}")
    public ApiResponse purchaseModify(@AuthenticationPrincipal TokenDto user, @ApiParam(value = "purchaseNo", required = true) @PathVariable("purchaseNo") Long purchaseNo, @RequestBody PurchaseDto.RequestModifyPurchaseForm form) {
        try {
            PurchaseDto purchaseDto = modelMapper.map(form, PurchaseDto.class);
            purchaseDto.setPurchaseNo(purchaseNo);
            purchaseDto.setUserNo(user.getUserNo());
            purchaseService.modifyPurchase(purchaseDto);
            return ApiUtils.success(purchaseService.findPurchase(user.getUserNo(), purchaseNo));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

}
