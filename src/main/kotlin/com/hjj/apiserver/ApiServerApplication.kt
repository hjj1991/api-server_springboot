package com.hjj.apiserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@EnableCaching
//@EnableScheduling
@SpringBootApplication
class ApiServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiServerApplication::class.java, *args)
}
