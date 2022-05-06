package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.AccountBookDto;
import com.hjj.apiserver.service.AccountBookService;
import com.hjj.apiserver.util.CurrentUser;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"5. AccountBook"})
@Slf4j
@RestController
@AllArgsConstructor
public class AccountBookController {
    private final AccountBookService accountBookService;
    private final ModelMapper modelMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "가계부 생성", notes = "가계부를 생성 한다.")
    @PostMapping("/account-book")
    public ApiResponse accountBookAdd(@CurrentUser UserEntity user, @Valid @RequestBody @ApiParam(value = "가계부 생성 객체", required = true) AccountBookDto.RequestAccountBookAddForm form) {
        try{
            AccountBookDto accountBookDto = modelMapper.map(form, AccountBookDto.class);

            accountBookService.addAccountBook(user, accountBookDto);

            return ApiUtils.success(null);
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인가계부 목록을 조회.", notes = "개인가계부를 조회 한다.")
    @GetMapping("/account-book")
    public ApiResponse accountBookList(@CurrentUser UserEntity user, @Valid AccountBookDto.RequestAccountBookFindAllForm form) {
        try{
            AccountBookDto accountBookDto = modelMapper.map(form, AccountBookDto.class);
            accountBookDto.setUserNo(user.getUserNo());
            return ApiUtils.success(accountBookService.findAllAccountBook(accountBookDto));
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인가계부 상세 조회.", notes = "개인가계부를 상세 조회 한다.")
    @GetMapping("/account-book/{accountBookNo}")
    public ApiResponse accountBookDetails(@CurrentUser UserEntity user, @PathVariable Long accountBookNo) {
        try{
            AccountBookDto accountBookDto = new AccountBookDto();
            accountBookDto.setUserNo(user.getUserNo());
            accountBookDto.setAccountBookNo(accountBookNo);

            return ApiUtils.success(accountBookService.findAccountBookDetail(accountBookDto));
        }catch (Exception e){
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE9999.getMsg(), ApiError.ErrCode.ERR_CODE9999);
        }
    }
}
