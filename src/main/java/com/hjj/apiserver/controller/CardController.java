package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError;
import com.hjj.apiserver.common.ApiResponse;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.common.provider.JwtTokenProvider;
import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.dto.CardDto;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.repositroy.CardRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import com.hjj.apiserver.service.CardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"2. Card"})
@RestController
@RequiredArgsConstructor
public class CardController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CardService cardService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인 카드 목록 조회", notes = "개인카드 목록조회한다..")
    @GetMapping("/card")
    public ApiResponse<List<CardDto>> getCardList(@AuthenticationPrincipal TokenDto user) {
        List<CardEntity> cardEntityList = cardRepository.findByUserEntity_UserNoAndDeleteYn(user.getUserNo(), 'N');
        List<CardDto> cardDto = cardEntityList.stream().map(tempCardEntity -> modelMapper.map(tempCardEntity, CardDto.class)).collect(Collectors.toList());

        return ApiUtils.success(cardDto);

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인 카드 등록", notes = "개인카드 등록한다.")
    @PostMapping("/card")
    public ApiResponse addCard(@AuthenticationPrincipal TokenDto user, @RequestBody CardDto.RequestAddCardForm requestAddCardForm) {
        try {
            CardDto cardDto = modelMapper.map(requestAddCardForm, CardDto.class);
            cardDto.setUserEntity(userRepository.getById(user.getUserNo()));
            cardService.insertCard(cardDto);
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "DELETE"
            ,value = "개인 카드 삭제"
            ,notes = "개인 카드를 삭제한다."
            ,responseContainer = "Integer")
    @DeleteMapping("/card/{cardNo}")
    public ApiResponse deleteCard(@AuthenticationPrincipal TokenDto user ,@ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo) {

        try {
            CardEntity cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            cardService.deleteCard(cardEntity);
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "PUT"
            ,value = "개인 카드 수정"
            ,notes = "개인 카드를 삭제한다."
            ,responseContainer = "Integer")
    @PutMapping("/card/{cardNo}")
    public ApiResponse updateCard(@AuthenticationPrincipal TokenDto user ,@ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo, @RequestBody CardDto.RequestModifyCardForm modifyCardForm) {
        try {
            CardEntity cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            cardService.updateCard(cardEntity, modifyCardForm.getCardDto());
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "GET"
            ,value = "개인 카드 상세"
            ,notes = "개인 카드 상세확인페이지"
            ,responseContainer = "Integer")
    @GetMapping("/card/{cardNo}")
    public ApiResponse selectCardDetail(@AuthenticationPrincipal TokenDto user ,@ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo) {
        try {
            CardEntity cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            CardDto cardDto = modelMapper.map(cardEntity, CardDto.class);
            return ApiUtils.success(cardDto);
        } catch (Exception e) {
            return ApiUtils.error(ApiError.ErrCode.ERR_CODE0005.getMsg(), ApiError.ErrCode.ERR_CODE0005);
        }

    }

}
