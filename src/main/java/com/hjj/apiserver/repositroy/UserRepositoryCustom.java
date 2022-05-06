package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.UserDto;

import java.util.Optional;

public interface UserRepositoryCustom {
    UserEntity findUserLeftJoinUserLogByUserNo(Long userNo);
}
