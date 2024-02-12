package com.hjj.apiserver.domain.user

class UserLog(
    userLogNo: Long = 0L,
    logType: LogType,
    user: User,
) {

    var userLogNo: Long = userLogNo
        private set
    var logType: LogType = logType
        private set
    var user: User = user
        private set
}