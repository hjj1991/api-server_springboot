package com.hjj.apiserver.persistence

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.application.port.out.user.WriteUserLogPort
import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMySqlDBContainer
import com.hjj.apiserver.converter.UserMapper
import com.hjj.apiserver.domain.user.LogType
import com.hjj.apiserver.domain.user.RoleType
import com.hjj.apiserver.domain.user.UserLog
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
    UserLogPersistenceAdapter::class,
    UserMapper::class,
    UserLogMapper::class,
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserLogPersistenceAdapterTest {
    @Autowired
    private lateinit var writeUserLogPort: WriteUserLogPort

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun insertUserLog_success() {
        // Given
        val savedUserEntity =
            userRepository.save(
                UserEntity(nickName = "테스트닉네임", userEmail = "test@test.com", userPw = "<PASSWORD>", roleType = RoleType.USER),
            )
        val user = userMapper.mapToDomainEntity(savedUserEntity)
        val userLog =
            UserLog(
                logType = LogType.SIGNUP,
                user = user,
            )

        // When
        val registerUserLog = writeUserLogPort.insertUserLog(userLog)
        // Then
        Assertions.assertThat(registerUserLog.userLogNo).isNotEqualTo(0L)
        Assertions.assertThat(registerUserLog.logType).isEqualTo(userLog.logType)
        Assertions.assertThat(registerUserLog.user).isEqualTo(user)
    }
}
