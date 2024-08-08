package com.hjj.apiserver.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import java.sql.DriverManager

@TestConfiguration("TestMySqlDBContainer")
class TestMySqlDBContainer {
    companion object {
        @Container
        @JvmStatic
        val container =
            MySQLContainer<Nothing>("mysql:latest")
                .apply {
                    withDatabaseName("test")
                    withUsername("root")
                    withPassword("root")
                    withPrivilegedMode(true)
                }

        init {
            // Use the container's URL to connect and run the SQL commands
            container.start()

            val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
            connection.prepareStatement("SET GLOBAL sql_mode = '';").execute() // Or use SET SESSION for testing purposes
            connection.close()
        }
    }
}
