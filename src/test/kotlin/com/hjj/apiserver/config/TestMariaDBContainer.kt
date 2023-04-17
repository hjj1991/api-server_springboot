package com.hjj.apiserver.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container

@TestConfiguration("TestMariaDBContainer")
class TestMariaDBContainer {

    companion object {
        @Container
        @JvmStatic
        val container = MariaDBContainer<Nothing>("mariadb:latest")
            .apply {
                withDatabaseName("test")
                withUsername("test")
                withPassword("test")
            }.apply { start() }
    }
}