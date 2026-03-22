package com.hjj.apiserver.application.port.out.auth

import java.time.OffsetDateTime

interface RecordLocalLoginSuccessPort {
    fun record(
        userId: Long,
        loggedInAt: OffsetDateTime,
    )
}
