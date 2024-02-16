package com.hjj.apiserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

// @EnableScheduling
@SpringBootApplication
class ApiServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiServerApplication::class.java, *args)
}
