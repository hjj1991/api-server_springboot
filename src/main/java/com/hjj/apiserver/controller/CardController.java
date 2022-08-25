package com.hjj.apiserver.controller;

import com.hjj.apiserver.common.ApiError_Java;
import com.hjj.apiserver.common.ApiResponse_Java;
import com.hjj.apiserver.common.ApiUtils;
import com.hjj.apiserver.domain.CardEntityJava;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.CardDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"2. Card"})
@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CardService cardService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인 카드 목록 조회", notes = "개인카드 목록조회한다..")
    @GetMapping("/card")
    public ApiResponse_Java<List<CardDto>> cardList(UserEntity user) {
        List<CardEntityJava> cardEntityList = cardRepository.findByUserEntity_UserNoAndDeleteYn(user.getUserNo(), 'N');
        List<CardDto> cardDto = cardEntityList.stream().map(tempCardEntity -> modelMapper.map(tempCardEntity, CardDto.class)).collect(Collectors.toList());

        return ApiUtils.success(cardDto);

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(value = "개인 카드 등록", notes = "개인카드 등록한다.")
    @PostMapping("/card")
    public ApiResponse_Java cardAdd(UserEntity user, @RequestBody CardDto.RequestAddCardForm requestAddCardForm) {
        try {
            CardDto cardDto = modelMapper.map(requestAddCardForm, CardDto.class);
            cardDto.setUserEntity(userRepository.getById(user.getUserNo()));
            cardService.insertCard(cardDto);
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0005.getMsg(), ApiError_Java.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "DELETE"
            ,value = "개인 카드 삭제"
            ,notes = "개인 카드를 삭제한다."
            ,responseContainer = "Integer")
    @DeleteMapping("/card/{cardNo}")
    public ApiResponse_Java cardRemove(UserEntity user , @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo) {

        try {
            CardEntityJava cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            cardService.deleteCard(cardEntity);
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0005.getMsg(), ApiError_Java.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "PUT"
            ,value = "개인 카드 수정"
            ,notes = "개인 카드를 삭제한다."
            ,responseContainer = "Integer")
    @PutMapping("/card/{cardNo}")
    public ApiResponse_Java cardModify(UserEntity user , @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo, @RequestBody CardDto.RequestModifyCardForm modifyCardForm) {
        try {
            CardEntityJava cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            cardService.updateCard(cardEntity, modifyCardForm.getCardDto());
            return ApiUtils.success(null);
        } catch (Exception e) {
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0005.getMsg(), ApiError_Java.ErrCode.ERR_CODE0005);
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")})
    @ApiOperation(httpMethod = "GET"
            ,value = "개인 카드 상세"
            ,notes = "개인 카드 상세확인페이지"
            ,responseContainer = "Integer")
    @GetMapping("/card/{cardNo}")
    public ApiResponse_Java cardDetails(UserEntity user , @ApiParam(value = "cardNo", required = true) @PathVariable("cardNo") Long cardNo) {
        try {
            CardEntityJava cardEntity = cardRepository.findByCardNoAndUserEntity_UserNo(cardNo, user.getUserNo()).orElseThrow(() -> new Exception("해당된 값이 없습니다."));
            CardDto cardDto = modelMapper.map(cardEntity, CardDto.class);
            return ApiUtils.success(cardDto);
        } catch (Exception e) {
            return ApiUtils.error(ApiError_Java.ErrCode.ERR_CODE0005.getMsg(), ApiError_Java.ErrCode.ERR_CODE0005);
        }

    }

}
