package com.hjj.apiserver.repository

import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMariaDBContainer
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(TestMariaDBContainer::class, DataSourceConfiguration::class, TestConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BaseRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    fun createUser(userId: String = "testId", nickName: String = "tsetNickName", role: Role = Role.USER): User {
        val user = User(
            userId = userId,
            nickName = nickName,
            role = role
        )
        return userRepository.save(user)
    }
}