package com.hjj.apiserver.service;

import com.hjj.apiserver.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired ModelMapper modelMapper;

    @Test
    public void 객체생성_테스트(){
        UserDto.RequestUserUpdateForm userUpdateForm = new UserDto.RequestUserUpdateForm();
        userUpdateForm.setUserNo(3L);

        UserDto userDto = modelMapper.map(userUpdateForm, UserDto.class);

        System.out.println("userDto = " + userDto);
    }

}