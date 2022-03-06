package com.hjj.apiserver.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError{
    private final String message;
    private final ErrCode errCode;

    public enum ErrCode {
        ERR_CODE0001("사용자가 없습니다."),
        ERR_CODE0002("해당 사용자가 존재합니다."),
        ERR_CODE0003("해당 닉네임이 존재합니다."),
        ERR_CODE0004("회원가입이 실패되었습니다."),
        ERR_CODE0005("카드 등록이 실패되었습니다.");


        private final String msg;

        ErrCode(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return msg;
        }
    }

    public ApiError(String message, ErrCode errCode) {
        this.message = message;
        this.errCode = errCode;
    }
}