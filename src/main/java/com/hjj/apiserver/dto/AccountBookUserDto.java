package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;

@Data
public class AccountBookUserDto {

    private Long accountBookUserNo;
    private Long accountBookNo;
    private Long userNo;
    private String backGroundColor;
    private String color;
    private AccountBookEntity accountBookInfo;
    private UserEntity userInfo;
    private AccountBookUserEntity.AccountRole accountRole = AccountBookUserEntity.AccountRole.GUEST;

    public AccountBookUserEntity toEntity(){
        AccountBookUserEntity accountBookUserEntity = AccountBookUserEntity.builder()
                .accountRole(accountRole)
                .accountBookInfo(accountBookInfo)
                .color(color)
                .backGroundColor(backGroundColor)
                .userInfo(userInfo)
                .build();

        return accountBookUserEntity;
    }
}
