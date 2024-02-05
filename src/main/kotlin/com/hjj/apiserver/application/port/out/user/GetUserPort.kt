package com.hjj.apiserver.application.port.out.user

interface GetUserPort {
    fun findExistsUserNickName(nickName: String): Boolean
}