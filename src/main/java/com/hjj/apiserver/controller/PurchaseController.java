package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError_Java;
import com.hjj.apiserver.common.ApiResponse_Java;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.PurchaseDto;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"3. Purchase"})
@RestController
@AllArgsConstructor
@Slf4j
public class PurchaseController {

    private final ModelMapper modelMapper;
    private final PurchaseService purchaseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 등록", notes = "지출, 수입을 등록한다.")
    @PostMapping("/purchase")
    public ApiResponse_Java purchaseAdd(UserEntity user, @RequestBody PurchaseDto.RequestAddPurchaseForm requestAddPurchaseForm) {
        try {
            PurchaseDto purchaseDto = modelMapper.map(requestAddPurchaseForm, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());

            purchaseService.addPurchase(purchaseDto);

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("addPurchase: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 리스트", notes = "지출, 수입을 리스트를 불러온다.")
    @GetMapping("/purchase")
    public ApiResponse_Java purchaseList(UserEntity user, PurchaseDto.RequestPurchaseFindForm form) {
        try {
            PageRequest pageRequest = PageRequest.of(form.getPage(), form.getSize());
            PurchaseDto purchaseDto = modelMapper.map(form, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());

            return ApiUtils.success(purchaseService.findPurchaseListOfPage(purchaseDto, pageRequest));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 삭제", notes = "지출, 수입을 삭제한다.")
    @DeleteMapping("/purchase/{purchaseNo}")
    public ApiResponse_Java purchaseRemove(UserEntity user, @PathVariable("purchaseNo") Long purchaseNo) {
        try {

            purchaseService.deletePurchase(user.getUserNo(), purchaseNo);


            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출, 수입 상세조회", notes = "지출, 수입을 상세 조회한다.")
    @GetMapping("/purchase/{purchaseNo}")
    public ApiResponse_Java purchaseDetails(UserEntity user, @PathVariable("purchaseNo") Long purchaseNo) {
        try {
            return ApiUtils.success(purchaseService.findPurchase(user.getUserNo(), purchaseNo));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출, 수입 수정", notes = "지출, 수입을 수정한다.")
    @PatchMapping("/purchase/{purchaseNo}")
    public ApiResponse_Java purchaseModify(UserEntity user, @ApiParam(value = "purchaseNo", required = true) @PathVariable("purchaseNo") Long purchaseNo, @RequestBody PurchaseDto.RequestModifyPurchaseForm form) {
        try {
            PurchaseDto purchaseDto = modelMapper.map(form, PurchaseDto.class);
            purchaseDto.setPurchaseNo(purchaseNo);
            purchaseDto.setUserNo(user.getUserNo());
            purchaseService.modifyPurchase(purchaseDto);
            return ApiUtils.success(purchaseService.findPurchase(user.getUserNo(), purchaseNo));
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE9999.getMsg(), ApiError_Java.ErrCode.ERR_CODE9999);
        }
    }

}
