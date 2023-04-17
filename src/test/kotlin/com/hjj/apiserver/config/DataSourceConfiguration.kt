package com.hjj.apiserver.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.testcontainers.containers.MariaDBContainer

@TestConfiguration
class DataSourceConfiguration {

    @Bean
    @DependsOn("TestMariaDBContainer")
    fun dataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .url(TestMariaDBContainer.container.jdbcUrl)
            .username(TestMariaDBContainer.container.username)
            .password(TestMariaDBContainer.container.password)
            .build()
    }
}