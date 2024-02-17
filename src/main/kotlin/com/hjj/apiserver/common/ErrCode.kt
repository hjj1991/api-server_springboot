package com.hjj.apiserver.common

import jakarta.annotation.PostConstruct
import org.springframework.context.MessageSource
import java.util.Locale

enum class ErrCode(
    var msg: String,
) {
    ERR_CODE0001("사용자가 없습니다."),
    ERR_CODE0002("해당 사용자가 존재합니다."),
    ERR_CODE0003("해당 닉네임이 존재합니다."),
    ERR_CODE0004("회원가입이 실패되었습니다."),
    ERR_CODE0005("카드 등록이 실패되었습니다."),
    ERR_CODE0006("가입한 계정이 존재합니다."),
    ERR_CODE0007("간편 로그인 계정입니다. \n 간편로그인을 이용해주세요."),
    ERR_CODE0008("계정 또는 패스워드를 확인해주세요."),
    ERR_CODE0009("유효한 간편계정 종류가 아닙니다."),
    ERR_CODE0010("해당 가계부가 존재하지 않습니다."),
    ERR_CODE0011("존재하지 않는 상위 카테고리입니다."),
    ERR_CODE0012("해당 지출 또는 수입 내역이 존재하지 않습니다."),
    ERR_CODE0013("로그인이 필요합니다. 로그인 해주세요."),
    ERR_CODE9999("서버 에러입니다."),
    ;

    //    @Component
    class ErrorInjector(
        private val messageSource: MessageSource,
    ) {
        @PostConstruct
        fun postConstruct() {
            ErrCode.values().forEach { it.msg = messageSource.getMessage(it.name, null, Locale.KOREA) }
        }
    }
}
