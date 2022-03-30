package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.AccountBookDto;
import com.hjj.apiserver.dto.AccountBookUserDto;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.AccountBookRepository;
import com.hjj.apiserver.repositroy.AccountBookUserRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AccountBookServiceTest {

    @Autowired private AccountBookRepository accountBookRepository;
    @Autowired private AccountBookUserRepository accountBookUserRepository;
    @Autowired private UserRepository userRepository;

    @Test
    UserEntity create_basic_user(){
        UserDto userDto = new UserDto();
        userDto.setUserId("test");
        userDto.setUserEmail("test@test.co.kr");
        userDto.setUserPw("testtest1234");
        userDto.setNickName("테스터");

        UserEntity userEntity = userDto.toEntity();

        return userRepository.save(userEntity);

    }

    @Test
    void 가계부_생성(){



        AccountBookDto accountBookDto = new AccountBookDto();
        accountBookDto.setAccountBookName("개인 가계부");
        accountBookDto.setAccountBookDesc("개인 가계부 설명");

        AccountBookEntity accountBookEntity = accountBookDto.toEntity();

        accountBookRepository.save(accountBookEntity);

        UserEntity userEntity = create_basic_user();

        AccountBookUserDto accountBookUserDto = new AccountBookUserDto();
        accountBookUserDto.setAccountBookInfo(accountBookEntity);
        accountBookUserDto.setUserInfo(userEntity);
        accountBookUserDto.setAccountRole(AccountBookUserEntity.AccountRole.OWNER);

        AccountBookUserEntity accountBookUserEntity = accountBookUserDto.toEntity();
        accountBookUserRepository.save(accountBookUserEntity);

        Assertions.assertEquals(accountBookUserEntity.getUserInfo(), userEntity);
        Assertions.assertEquals(accountBookUserEntity.getAccountBookInfo(), accountBookEntity);
        Assertions.assertEquals(accountBookUserEntity.getAccountRole(), AccountBookUserEntity.AccountRole.OWNER);



    }

}