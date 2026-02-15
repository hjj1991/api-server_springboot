package com.hjj.apiserver.config

import co.elastic.clients.elasticsearch.ElasticsearchClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories
class ElasticsearchConfig(
    @Value("\${spring.elasticsearch.uris}")
    private val elasticsearchUris: String,
    @Value("\${spring.elasticsearch.username:}")
    private val username: String,
    @Value("\${spring.elasticsearch.password:}")
    private val password: String,
) : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        val uris = elasticsearchUris.split(",").map { it.trim() }.toTypedArray()
        return if (username.isNotEmpty() && password.isNotEmpty()) {
            ClientConfiguration.builder()
                .connectedTo(*uris)
                .withBasicAuth(username, password)
                .build()
        } else {
            ClientConfiguration.builder()
                .connectedTo(*uris)
                .build()
        }
    }

    @Bean
    @Primary
    fun elasticsearchTemplate(client: ElasticsearchClient): ElasticsearchTemplate = ElasticsearchTemplate(client)
}
