package com.hjj.apiserver.application.port.out.user

import com.hjj.apiserver.domain.user.UserLog

interface WriteUserLogPort {
    fun registerUserLog(userLog: UserLog)
}