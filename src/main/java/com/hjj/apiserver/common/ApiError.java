package com.hjj.apiserver.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;

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
        ERR_CODE0005("카드 등록이 실패되었습니다."),
        ERR_CODE0006("가입한 계정이 존재합니다."),
        ERR_CODE0007("간편 로그인 계정입니다. \n 간편로그인을 이용해주세요."),
        ERR_CODE9999("잘못된 요청입니다.");


        private String msg;

        ErrCode(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return msg;
        }
    }

    @RequiredArgsConstructor
    @Component
    public static class ErrorInjector {
        private final MessageSource messageSource;

        @PostConstruct
        public void postConstruct(){
            Arrays.stream(ErrCode.values()).forEach(errCode1 -> errCode1.msg = messageSource.getMessage(errCode1.name(), null, Locale.KOREA));
        }
    }

    public ApiError(String message, ErrCode errCode) {
        this.message = message;
        this.errCode = errCode;
    }
}