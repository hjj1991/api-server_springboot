package com.hjj.apiserver.persistence

import com.hjj.apiserver.adapter.out.persistence.user.UserPersistenceAdapter
import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMySqlDBContainer
import com.hjj.apiserver.converter.UserMapper
import com.hjj.apiserver.domain.user.SnsAccount
import com.hjj.apiserver.domain.user.SnsAccountStatusType
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.RoleType
import com.hjj.apiserver.domain.user.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(
    TestMySqlDBContainer::class,
    DataSourceConfiguration::class,
    TestConfiguration::class,
    CredentialPersistenceAdapter::class,
    CredentialMapper::class,
    UserMapper::class,
    UserPersistenceAdapter::class,
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CredentialPersistenceAdapterTest {
    @Autowired
    private lateinit var writeCredentialPort: WriteCredentialPort

    @Autowired
    private lateinit var getCredentialPort: GetCredentialPort

    @Autowired
    private lateinit var writeUserPort: WriteUserPort

    @Test
    fun registerCredential_success() {
        // Given
        val user = User(nickName = "테스트닉네임", userEmail = "test@test.com", userPw = "<PASSWORD>", role = RoleType.USER)
        val savedUser = writeUserPort.insertUser(user)
        val snsAccount =
            SnsAccount(
                userId = "tester",
                credentialEmail = "tester@tester.com",
                provider = Provider.GENERAL,
                user = savedUser,
                state = SnsAccountStatusType.CONNECTED,
            )
        // When
        val registerCredential = writeCredentialPort.registerCredential(snsAccount)
        // Then
        Assertions.assertThat(registerCredential.credentialNo).isNotEqualTo(0L)
        Assertions.assertThat(registerCredential.userId).isEqualTo(snsAccount.userId)
        Assertions.assertThat(registerCredential.credentialEmail).isEqualTo(snsAccount.credentialEmail)
        Assertions.assertThat(registerCredential.provider).isEqualTo(snsAccount.provider)
        Assertions.assertThat(registerCredential.user).isEqualTo(snsAccount.user)
    }

    @Test
    fun findExistsCredentialByUserIdAndProvider_success() {
        // Given
        val user = User(nickName = "테스트닉네임", userEmail = "test@test.com", userPw = "<PASSWORD>", role = RoleType.USER)
        val savedUser = writeUserPort.insertUser(user)
        val snsAccount =
            SnsAccount(
                userId = "tester",
                credentialEmail = "tester@tester.com",
                provider = Provider.GENERAL,
                user = savedUser,
                state = SnsAccountStatusType.CONNECTED,
            )
        writeCredentialPort.registerCredential(snsAccount)
        // When
        val existsCredentialByUserIdAndProvider =
            getCredentialPort.findExistsCredentialByUserIdAndProvider(
                snsAccount.userId,
                snsAccount.provider,
            )
        // Then
        Assertions.assertThat(existsCredentialByUserIdAndProvider).isEqualTo(true)
    }

    @Test
    fun findExistsCredentialByUserIdAndProvider_fail_when_equalUserIdAndNotEqualProvider_then_false() {
        // Given
        val user = User(nickName = "테스트닉네임", userEmail = "test@test.com", userPw = "<PASSWORD>", role = RoleType.USER)
        val savedUser = writeUserPort.insertUser(user)
        val snsAccount =
            SnsAccount(
                userId = "tester",
                credentialEmail = "tester@tester.com",
                provider = Provider.NAVER,
                user = savedUser,
                state = SnsAccountStatusType.CONNECTED,
            )
        writeCredentialPort.registerCredential(snsAccount)
        // When
        val existsCredentialByUserIdAndProvider =
            getCredentialPort.findExistsCredentialByUserIdAndProvider(
                snsAccount.userId,
                Provider.GENERAL,
            )
        // Then
        Assertions.assertThat(existsCredentialByUserIdAndProvider).isEqualTo(false)
    }
}
