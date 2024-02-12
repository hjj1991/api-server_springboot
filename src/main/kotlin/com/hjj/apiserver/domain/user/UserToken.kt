package com.hjj.apiserver.domain.user

class UserToken(
    userNo: Long,
    accessToken: String,
    refreshToken: String,
) {
    var userNo: Long = userNo
    var accessToken: String = accessToken
    var refreshToken: String = refreshToken
}