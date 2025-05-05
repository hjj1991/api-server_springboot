package com.hjj.apiserver.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMySqlDBContainer
import com.hjj.apiserver.domain.user.RoleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(TestMySqlDBContainer::class, DataSourceConfiguration::class, TestConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BaseRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

    fun createUser(
        userId: String = "testId",
        nickName: String = "tsetNickName",
        roleType: RoleType = RoleType.USER,
    ): UserEntity {
        val userEntity =
            UserEntity(
                nickName = nickName,
                roleType = roleType,
            )
        return userRepository.save(userEntity)
    }
}
