package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.dto.AccountBookDto;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.service.AccountBookService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
public class AccountBookController {
    private final AccountBookService accountBookService;
    private final ModelMapper modelMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "가계부 생성", notes = "가계부를 생성 한다.")
    @PostMapping("/account-book")
    public ApiResponse accountBookAdd(@AuthenticationPrincipal TokenDto tokenDto, @Valid @RequestBody @ApiParam(value = "가계부 생성 객체", required = true) AccountBookDto.RequestAccountBookAddForm form) {
        try{
            AccountBookDto accountBookDto = modelMapper.map(form, AccountBookDto.class);
            accountBookDto.setUserNo(tokenDto.getUserNo());

            accountBookService.addAccountBook(accountBookDto);

            return ApiUtils.success(null);
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0004.getMsg(), ApiError.ErrCode.ERR_CODE0004);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인가계부 목록을 조회.", notes = "개인가계부를 조회 한다.")
    @GetMapping("/account-book")
    public ApiResponse accountBookFindAll(@AuthenticationPrincipal TokenDto tokenDto, @Valid AccountBookDto.RequestAccountBookFindAllForm form) {
        try{
            AccountBookDto accountBookDto = modelMapper.map(form, AccountBookDto.class);
            accountBookDto.setUserNo(tokenDto.getUserNo());
            return ApiUtils.success(accountBookService.findAllAccountBook(accountBookDto));
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0004.getMsg(), ApiError.ErrCode.ERR_CODE0004);
        }
    }
}
