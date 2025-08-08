package com.hjj.apiserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class ScheduleTaskConfig {

    @Bean
    fun sseTaskScheduler(): ThreadPoolTaskScheduler {
        return ThreadPoolTaskScheduler().apply {
            poolSize = 2
            threadNamePrefix = "sse-heartbeat-"
            isDaemon = true
            setRemoveOnCancelPolicy(true) // 취소된 작업 큐에서 빨리 제거
            initialize()
        }
    }
}
