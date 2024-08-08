package com.hjj.apiserver.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn

@TestConfiguration
class DataSourceConfiguration {
    @Bean
    @DependsOn("TestMySqlDBContainer")
    fun dataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .url(TestMySqlDBContainer.container.jdbcUrl)
            .username(TestMySqlDBContainer.container.username)
            .password(TestMySqlDBContainer.container.password)
            .build()
    }
}
