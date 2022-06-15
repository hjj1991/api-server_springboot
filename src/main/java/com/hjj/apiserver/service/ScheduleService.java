package com.hjj.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class ScheduleService {
    private final WebScrappingService webScrappingService;

    @Scheduled(fixedRate = 100000, initialDelay = 5000)
    public void scheduleliivMateTodayQuizTask(){
        try{
            log.info("scheduleliivMateTodayQuizTask: {}", LocalDateTime.now());
            webScrappingService.liivMateTodayQuizAnswerFind();
        }catch (Exception e){
            log.error("scheduleliivMateTodayQuizTask: {}", e);
        }

    }
}