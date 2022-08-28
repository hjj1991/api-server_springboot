package com.hjj.apiserver.service

import com.hjj.apiserver.dto.user.request.UserSinUpRequest
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.service.UserService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
) {

    @Test
    @DisplayName("회원가입")
    fun signUpTest(){
        // given
        val request = UserSinUpRequest("testUser12", "뜨끔이당", "testUser@naver.com", "testPassword12#$")

        // when
        val savedUser = userService.signUp(request)


        //then
        val foundUser = userRepository.findByUserId("testUser12")
        Assertions.assertThat(foundUser!!.nickName).isEqualTo("뜨끔이당")
        Assertions.assertThat(foundUser!!.userEmail).isEqualTo("testUser@naver.com")
    }
}