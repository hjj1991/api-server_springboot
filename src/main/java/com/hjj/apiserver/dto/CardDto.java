package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hjj.apiserver.domain.CardEntityJava;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;

@Data
public class CardDto {

    private Long cardNo;
    private String cardName;
    private CardEntityJava.CardType cardType;
    private String cardDesc;
    @JsonIgnore
    private UserEntity userEntity;


    public CardEntityJava toEntity() {
        return CardEntityJava.builder()
                .cardName(cardName)
                .cardType(cardType)
                .cardDesc(cardDesc)
                .userEntity(userEntity)
                .build();
    }

    @Data
    public static class RequestAddCardForm{
        private String cardName;
        private CardEntityJava.CardType cardType;
        private String cardDesc;
    }

    @Data
    public static class RequestModifyCardForm{
        private String cardName;
        private CardEntityJava.CardType cardType;
        private String cardDesc;

        public CardDto getCardDto(){
            CardDto cardDto = new CardDto();
            cardDto.setCardName(cardName);
            cardDto.setCardType(cardType);
            cardDto.setCardDesc(cardDesc);
            return cardDto;
        }
    }

}
