package com.hjj.apiserver.adapter.out.persistence.auth

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.OffsetDateTime
import java.util.UUID
import javax.sql.DataSource

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=none",
    ],
)
class AuthSchemaSqlIntegrationTest {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setUp() {
        ResourceDatabasePopulator(ClassPathResource("sql/auth-schema.sql")).execute(dataSource)
        jdbcTemplate.execute(
            """
            TRUNCATE TABLE
                user_deletion_requests,
                user_roles,
                roles,
                local_credentials,
                auth_identities,
                users
            RESTART IDENTITY CASCADE
            """.trimIndent(),
        )
    }

    @Test
    fun `활성 사용자 이메일 해시는 중복될 수 없다`() {
        insertUser(emailLookupHash = "a".repeat(64), status = "ACTIVE")

        assertThatThrownBy {
            insertUser(emailLookupHash = "a".repeat(64), status = "ACTIVE")
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `동일 provider subject hash 는 중복될 수 없다`() {
        val userId = insertUser(emailLookupHash = "a".repeat(64), status = "ACTIVE")
        insertAuthIdentity(userId = userId, providerType = "LOCAL", loginId = "hello-user", providerSubjectHash = "b".repeat(64))

        assertThatThrownBy {
            insertAuthIdentity(userId = userId, providerType = "LOCAL", loginId = "other-user", providerSubjectHash = "b".repeat(64))
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `동일 local login_id 는 중복될 수 없다`() {
        val firstUserId = insertUser(emailLookupHash = "f".repeat(64), status = "ACTIVE")
        val secondUserId = insertUser(emailLookupHash = "g".repeat(64), status = "ACTIVE")
        insertAuthIdentity(userId = firstUserId, providerType = "LOCAL", loginId = "hello-user", providerSubjectHash = "h".repeat(64))

        assertThatThrownBy {
            insertAuthIdentity(userId = secondUserId, providerType = "LOCAL", loginId = "hello-user", providerSubjectHash = "i".repeat(64))
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `동일 사용자의 pending 삭제 요청은 하나만 허용한다`() {
        val userId = insertUser(emailLookupHash = "c".repeat(64), status = "PENDING_DELETION")
        insertDeletionRequest(userId = userId, status = "PENDING")

        assertThatThrownBy {
            insertDeletionRequest(userId = userId, status = "PENDING")
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `삭제 요청이 취소되면 새 pending 요청을 다시 만들 수 있다`() {
        val userId = insertUser(emailLookupHash = "d".repeat(64), status = "PENDING_DELETION")
        val requestId = insertDeletionRequest(userId = userId, status = "PENDING")

        jdbcTemplate.update(
            """
            UPDATE user_deletion_requests
            SET status = ?, cancelled_at = ?, modified_at = ?
            WHERE id = ?
            """.trimIndent(),
            "CANCELLED",
            OffsetDateTime.parse("2026-03-22T03:00:00Z"),
            OffsetDateTime.parse("2026-03-22T03:00:00Z"),
            requestId,
        )

        insertDeletionRequest(userId = userId, status = "PENDING")
    }

    @Test
    fun `사용자 token version 기본값은 0이다`() {
        val userId = insertUser(emailLookupHash = "e".repeat(64), status = "ACTIVE")

        val tokenVersion =
            jdbcTemplate.queryForObject(
                "SELECT token_version FROM users WHERE id = ?",
                Long::class.java,
                userId,
            )

        assertThat(tokenVersion).isEqualTo(0L)
    }

    private fun insertUser(emailLookupHash: String, status: String): Long =
        jdbcTemplate.queryForObject(
            """
            INSERT INTO users (
                display_name,
                email_ciphertext,
                email_lookup_hash,
                email_verified_at,
                status
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """.trimIndent(),
            Long::class.java,
            "테스트 사용자 ${UUID.randomUUID()}",
            "cipher:${UUID.randomUUID()}",
            emailLookupHash,
            OffsetDateTime.parse("2026-03-22T00:00:00Z"),
            status,
        )!!

    private fun insertAuthIdentity(userId: Long, providerType: String, loginId: String?, providerSubjectHash: String): Long =
        jdbcTemplate.queryForObject(
            """
            INSERT INTO auth_identities (
                user_id,
                provider_type,
                login_id,
                provider_subject_hash,
                status,
                linked_at
            )
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """.trimIndent(),
            Long::class.java,
            userId,
            providerType,
            loginId,
            providerSubjectHash,
            "ACTIVE",
            OffsetDateTime.parse("2026-03-22T00:00:00Z"),
        )!!

    private fun insertDeletionRequest(userId: Long, status: String): Long =
        jdbcTemplate.queryForObject(
            """
            INSERT INTO user_deletion_requests (
                user_id,
                status,
                requested_at,
                scheduled_purge_at
            )
            VALUES (?, ?, ?, ?)
            RETURNING id
            """.trimIndent(),
            Long::class.java,
            userId,
            status,
            OffsetDateTime.parse("2026-03-22T00:00:00Z"),
            OffsetDateTime.parse("2026-03-29T00:00:00Z"),
        )!!

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:16")

        @JvmStatic
        @DynamicPropertySource
        fun registerDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }
}
