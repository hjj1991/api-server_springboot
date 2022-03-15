package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.domain.UserLogEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLogDto {


    private Long logNo;
    private LocalDateTime loginDateTime;
    private UserLogEntity.SignInType signInType;
    private UserLogEntity.LogType logType;
    private UserEntity userInfo;
    private LocalDateTime createdDate;

    public UserLogEntity toEntity(){
        return UserLogEntity.builder()
                .userInfo(userInfo)
                .logType(logType)
                .loginDateTime(loginDateTime)
                .createdDate(LocalDateTime.now())
                .signInType(signInType)
                .build();
    }

}
