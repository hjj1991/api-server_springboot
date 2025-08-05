package com.hjj.apiserver.config

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Configuration
@EnableElasticsearchRepositories
class ElasticsearchConfig(
    @Value("\${spring.elasticsearch.uris}")
    private val elasticsearchUris: String,

    @Value("\${spring.elasticsearch.username:}")
    private val username: String,

    @Value("\${spring.elasticsearch.password:}")
    private val password: String,
) {
    @Bean
    @Primary
    fun elasticsearchRestClient(): RestClient {
        return try {
            val uriArray = elasticsearchUris.split(",")
            val hosts = uriArray.map { it.trim() }
                .map { HttpHost.create(it) }
                .toTypedArray()

            val builder = RestClient.builder(*hosts)

            // HTTPS인 경우 SSL 검증 비활성화
            if (elasticsearchUris.contains("https://")) {
                builder.setHttpClientConfigCallback { httpClientBuilder ->
                    try {
                        val sslContext = SSLContext.getInstance("TLS")
                        sslContext.init(
                            null,
                            arrayOf(object : X509TrustManager {
                                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                            }),
                            SecureRandom()
                        )

                        httpClientBuilder.setSSLContext(sslContext)
                        httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    } catch (e: Exception) {
                        throw RuntimeException("SSL 설정 실패", e)
                    }

                    // 인증 설정
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val credentialsProvider = BasicCredentialsProvider()
                        credentialsProvider.setCredentials(
                            AuthScope.ANY,
                            UsernamePasswordCredentials(username, password)
                        )
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                    }

                    httpClientBuilder
                }
            } else {
                // HTTP인 경우 기본 인증만 설정
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    builder.setHttpClientConfigCallback { httpClientBuilder ->
                        val credentialsProvider = BasicCredentialsProvider()
                        credentialsProvider.setCredentials(
                            AuthScope.ANY,
                            UsernamePasswordCredentials(username, password)
                        )
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                        httpClientBuilder
                    }
                }
            }

            builder.build()
        } catch (e: Exception) {
            throw RuntimeException("Elasticsearch RestClient 생성 실패", e)
        }
    }

    @Bean
    @Primary
    fun elasticsearchClient(restClient: RestClient): ElasticsearchClient {
        val transport = RestClientTransport(restClient, JacksonJsonpMapper())
        return ElasticsearchClient(transport)
    }

    @Bean
    @Primary
    fun elasticsearchTemplate(client: ElasticsearchClient): ElasticsearchTemplate {
        return ElasticsearchTemplate(client)
    }
}
