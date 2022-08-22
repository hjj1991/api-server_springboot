package com.hjj.apiserver.dto.user.response

class NaverProfileResponse {
    val resultcode: String? = null
    val message: String? = null
    val response: Response? = null


    class Response {
        val id: String? = null
        val nickname: String? = null
        val name: String? = null
        val email: String? = null
        val gender: String? = null
        val age: String? = null
        val birthday: String? = null
        val profile_image: String? = null
    }
}