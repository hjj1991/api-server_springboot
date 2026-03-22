package com.hjj.apiserver.adapter.input.web.admin

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.junit.jupiter.api.BeforeEach
import java.time.Instant

@SpringBootTest(
    properties = [
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=none",
    ],
)
class AdminOverviewSecurityIntegrationTest {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc =
            MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
    }

    @Test
    fun `비로그인 사용자는 관리자 개요 조회에 접근할 수 없다`() {
        mockMvc.perform(
            get("/admin/overview")
                .header("API-Version", "1.0"),
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `USER 권한만 있는 사용자는 관리자 개요 조회에 접근할 수 없다`() {
        mockMvc.perform(
            get("/admin/overview")
                .header("API-Version", "1.0")
                .with(
                    jwt().jwt { token ->
                        token.subject("10")
                        token.claim("jti", "access-jti-user")
                        token.issuedAt(Instant.parse("2026-03-22T03:00:00Z"))
                        token.expiresAt(Instant.parse("2026-03-22T03:10:00Z"))
                        token.claim("ver", 0)
                        token.claim("roles", listOf("USER"))
                    }.authorities(SimpleGrantedAuthority("ROLE_USER")),
                ),
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `ADMIN 권한 사용자는 관리자 개요 조회에 접근할 수 있다`() {
        mockMvc.perform(
            get("/admin/overview")
                .header("API-Version", "1.0")
                .with(
                    jwt().jwt { token ->
                        token.subject("11")
                        token.claim("jti", "access-jti-admin")
                        token.issuedAt(Instant.parse("2026-03-22T03:00:00Z"))
                        token.expiresAt(Instant.parse("2026-03-22T03:10:00Z"))
                        token.claim("ver", 0)
                        token.claim("roles", listOf("ADMIN", "USER"))
                    }.authorities(
                        SimpleGrantedAuthority("ROLE_ADMIN"),
                        SimpleGrantedAuthority("ROLE_USER"),
                    ),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(11))
            .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
            .andExpect(jsonPath("$.sections[0]").value("content"))
    }
}
