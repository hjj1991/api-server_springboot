package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.AccountBookEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Data
public class AccountBookDto {

    private Long accountBookNo;
    private Long userNo;
    private String accountBookName;
    private String accountBookDesc;
    private LocalDate startDate;
    private LocalDate endDate;


    public AccountBookEntity toEntity(){
        AccountBookEntity accountBookEntity = AccountBookEntity.builder()
                .accountBookName(accountBookName)
                .accountBookDesc(accountBookDesc)
                .build();

        return accountBookEntity;
    }

    @Data
    public static class RequestAccountBookAddForm{

        @NotBlank
        private String accountBookName;
        private String accountBookDesc;
    }

    @Data
    public static class RequestAccountBookFindAllForm{
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Data
    public static class ResponseAccountBookFindAll{
        private Long accountBookNo;
        private String accountBookName;
        private String accountBookDesc;
        private int totalIncomeAmount;
        private int totalOutgoingAmount;
        private List<JoinedUser> joinedUserList;

        @Data
        public static class JoinedUser{
            private String nickName;
            private String picture;
        }
    }
}
