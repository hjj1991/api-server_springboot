package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.AccountBookEntityJava;
import com.hjj.apiserver.domain.AccountBookUserEntityJava;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;

@Data
public class AccountBookUserDto {

    private Long accountBookUserNo;
    private Long accountBookNo;
    private Long userNo;
    private String backGroundColor;
    private String color;
    private AccountBookEntityJava accountBookEntity;
    private UserEntity userEntity;
    private AccountBookUserEntityJava.AccountRole accountRole = AccountBookUserEntityJava.AccountRole.GUEST;

    public AccountBookUserEntityJava toEntity(){
        AccountBookUserEntityJava accountBookUserEntity = AccountBookUserEntityJava.builder()
                .accountRole(accountRole)
                .accountBookEntity(accountBookEntity)
                .color(color)
                .backGroundColor(backGroundColor)
                .userEntity(userEntity)
                .build();

        return accountBookUserEntity;
    }
}
