package com.hjj.apiserver.common.exception.financial

class FinancialProductNotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)

    // 이미 알고 있는 CUSTOM 예외이기 떄문에 호출 스택을 순회하면 비용 낭비이므로 재정의
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
