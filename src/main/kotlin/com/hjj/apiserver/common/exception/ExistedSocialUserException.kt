package com.hjj.apiserver.common.exception

class ExistedSocialUserException: Exception() {

    /* 이미 알고 있는 CUSTOM 예외이기 떄문에 호출 스택을 순회하면 비용 낭비이므로 재정의 */
    override fun fillInStackTrace(): Throwable {
        return this
    }
}