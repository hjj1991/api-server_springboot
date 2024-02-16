package com.hjj.apiserver.service.impl

import com.hjj.apiserver.util.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduleService(
    private val webScrappingService: WebScrappingService,
) {
    private val log = logger()

    @Scheduled(cron = "0 00 14 * * *")
    fun scheduleliivMateTodayQuizTask() {
        try {
            log.info("scheduleliivMateTodayQuizTask: {}", LocalDateTime.now())
            webScrappingService.liivMateTodayQuizAnswerFind()
        } catch (e: Exception) {
            log.error("scheduleliivMateTodayQuizTask: {}", e)
        }
    }
}
