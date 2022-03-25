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
    private AccountBookEntity accountBookInfo;
    private UserEntity userInfo;
    private AccountBookUserEntity.AccountRole accountRole = AccountBookUserEntity.AccountRole.USER;

    public AccountBookUserEntity toEntity(){
        AccountBookUserEntity accountBookUserEntity = AccountBookUserEntity.builder()
                .accountRole(accountRole)
                .accountBookInfo(accountBookInfo)
                .userInfo(userInfo)
                .build();

        return accountBookUserEntity;
    }
}
