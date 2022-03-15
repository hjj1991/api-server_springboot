package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.domain.UserLogEntity;
import com.hjj.apiserver.dto.UserLogDto;
import com.hjj.apiserver.repositroy.UserLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserLogService {

    private final UserLogRepository userLogRepository;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public UserLogEntity insertUserLog(UserLogDto userLogDto){
        return userLogRepository.save(userLogDto.toEntity());
    }


}
