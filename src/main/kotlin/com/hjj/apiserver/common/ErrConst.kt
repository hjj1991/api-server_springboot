package com.hjj.apiserver.common

import org.springframework.http.HttpStatus

enum class ErrConst(
    val status: HttpStatus,
    val msg: String,
) {
    ERR_CODE0001(HttpStatus.NOT_FOUND, "사용자가 없습니다."),
    ERR_CODE0002(HttpStatus.CONFLICT, "해당 사용자가 존재합니다."),
    ERR_CODE0003(HttpStatus.CONFLICT, "해당 닉네임이 존재합니다."),
    ERR_CODE0004(HttpStatus.BAD_REQUEST, "회원가입이 실패되었습니다."),
    ERR_CODE0005(HttpStatus.BAD_REQUEST, "카드 등록이 실패되었습니다."),
    ERR_CODE0006(HttpStatus.CONFLICT, "가입한 계정이 존재합니다."),
    ERR_CODE0007(HttpStatus.BAD_REQUEST, "간편 로그인 계정입니다. \n 간편로그인을 이용해주세요."),
    ERR_CODE0008(HttpStatus.UNAUTHORIZED, "계정 또는 패스워드를 확인해주세요."),
    ERR_CODE0009(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ERR_CODE0010(HttpStatus.NOT_FOUND, "해당 가계부가 존재하지 않습니다."),
    ERR_CODE0011(HttpStatus.BAD_REQUEST, "존재하지 않는 상위 카테고리입니다."),
    ERR_CODE0012(HttpStatus.NOT_FOUND, "해당 지출 또는 수입 내역이 존재하지 않습니다."),
    ERR_CODE0013(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다. 로그인 해주세요."),
    ERR_CODE0014(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    ERR_CODE0015(HttpStatus.FORBIDDEN, "존재하지 않는 유저 권한입니다."),
    ERR_CODE0016(HttpStatus.BAD_REQUEST, "잘못된 요청 값입니다."),
    ERR_CODE0017(HttpStatus.BAD_REQUEST, "가입 인증이 유효하지 않거나 만료되었습니다."),
    ERR_CODE0018(HttpStatus.UNAUTHORIZED, "인증 세션이 유효하지 않습니다. 다시 로그인해주세요."),
    ERR_CODE9999(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),
    ;
}
