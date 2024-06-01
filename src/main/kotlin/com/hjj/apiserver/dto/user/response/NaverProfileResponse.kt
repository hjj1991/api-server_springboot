package com.hjj.apiserver.dto.user.response

class NaverProfileResponse(
    val resultcode: String,
    val message: String,
    val response: Response,
) {
    class Response(
        val id: String,
        val nickname: String,
        val name: String? = null,
        val email: String? = null,
        val gender: String? = null,
        val age: String? = null,
        val birthday: String? = null,
        val profile_image: String? = null,
    )

    fun isFail(): Boolean {
        return this.message != "success"
    }
}
