package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.service.CardService;
import com.hjj.apiserver.service.PurchaseService;
import com.hjj.apiserver.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Api(tags = {"3. Purchase"})
@RestController
@AllArgsConstructor
@Slf4j
public class PurchaseController {

    private final ModelMapper modelMapper;
    private final PurchaseService purchaseService;
    private final CardService cardService;
    private final CategoryService categoryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 등록", notes = "지출, 수입을 등록한다.")
    @PostMapping("/purchase")
    public ApiResponse addPurchase(@AuthenticationPrincipal TokenDto user, @RequestBody PurchaseDto.RequestAddPurchaseForm requestAddPurchaseForm) {
        try {
            PurchaseDto purchaseDto = modelMapper.map(requestAddPurchaseForm, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());

            purchaseService.addPurchase(purchaseDto);

            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("addPurchase: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 리스트", notes = "지출, 수입을 리스트를 불러온다.")
    @GetMapping("/purchase")
    public ApiResponse getPurchaseList(@AuthenticationPrincipal TokenDto user, PurchaseDto.RequestPurchaseFindForm form) {
        try {

            PurchaseDto purchaseDto = modelMapper.map(form, PurchaseDto.class);
            purchaseDto.setUserNo(user.getUserNo());
            PurchaseDto.ResponsePurchaseList responsePurchaseList = new PurchaseDto.ResponsePurchaseList();
            responsePurchaseList.setPurchaseList(purchaseService.findPurchaseList(purchaseDto));
            responsePurchaseList.setCardList(cardService.selectCardList(user.getUserNo()));
            responsePurchaseList.setCategoryList(categoryService.findCategory(user.getUserNo(), form.getAccountBookNo()));


            return ApiUtils.success(responsePurchaseList);
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "지출,수입 삭제", notes = "지출, 수입을 삭제한다.")
    @DeleteMapping("/purchase/{purchaseNo}")
    public ApiResponse deletePurchase(@AuthenticationPrincipal TokenDto user, @PathVariable("purchaseNo") Long purchaseNo) {
        try {

            purchaseService.deletePurchase(user.getUserNo(), purchaseNo);


            return ApiUtils.success(null);
        } catch (Exception e) {
            log.error("getPurchaseList: {}", e);
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }
}
