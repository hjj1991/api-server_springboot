package com.hjj.apiserver.persistence

import com.hjj.apiserver.adapter.out.persistence.user.UserPersistenceAdapter
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMariaDBContainer
import com.hjj.apiserver.converter.UserMapper
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(
    TestMariaDBContainer::class,
    DataSourceConfiguration::class,
    TestConfiguration::class,
    UserPersistenceAdapter::class,
    UserMapper::class,
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserPersistenceAdapterTest {
    @Autowired
    lateinit var getUserPort: GetUserPort

    @Autowired
    lateinit var writeUserPort: WriteUserPort

    @Test
    fun registerUserTest_success() {
        // Given
        val user =
            User(
                nickName = "nickname",
                userEmail = "test@example.com",
                userPw = "abc",
                picture = "haha",
                role = Role.USER,
            )

        // When
        val savedUser = writeUserPort.registerUser(user)

        // Then
        Assertions.assertThat(savedUser.userNo).isNotEqualTo(0L)
        Assertions.assertThat(savedUser.userPw).isEqualTo(user.userPw)
        Assertions.assertThat(savedUser.nickName).isEqualTo(user.nickName)
        Assertions.assertThat(savedUser.picture).isEqualTo(user.picture)
        Assertions.assertThat(savedUser.role).isEqualTo(user.role)
    }

    @Test
    fun findExistsUserNickNameTest_when_existsNickName_then_true() {
        // Given
        val nickName = "alreadyNickName"
        val user = User(nickName = nickName)
        writeUserPort.registerUser(user)

        // When
        val existsUserNickName = getUserPort.findExistsUserNickName(nickName)

        // Then
        Assertions.assertThat(existsUserNickName).isEqualTo(true)
    }

    @Test
    fun findExistsUserNickNameTest_when_notExistsNickName_then_false() {
        // Given
        val nickName = "notExistsNickName"

        // When
        val existsUserNickName = getUserPort.findExistsUserNickName(nickName)

        // Then
        Assertions.assertThat(existsUserNickName).isEqualTo(false)
    }
}
