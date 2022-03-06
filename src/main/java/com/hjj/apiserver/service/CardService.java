package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.dto.CardDto;
import com.hjj.apiserver.repositroy.CardRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void insertCard(CardDto cardDto){
        CardEntity cardEntity = cardDto.toEntity();
        cardRepository.save(cardEntity);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteCard(CardEntity cardEntity){
        cardEntity.delete();
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateCard(CardEntity cardEntity, CardDto cardDto){

        cardEntity.update(cardDto.getCardName(), cardDto.getCardType(), cardDto.getCardDesc());
    }

    public List<CardDto> selectCardList(Long userNo){
        List<CardEntity> cardEntityList = cardRepository.findByUserInfo_UserNoAndDeleteYn(userNo, 'N');
        List<CardDto> cardDtoList = new ArrayList<>();

        cardEntityList.stream().forEach(cardEntity -> {
            CardDto cardDto = modelMapper.map(cardEntity, CardDto.class);

            cardDtoList.add(cardDto);
        });
        return cardDtoList;
    }

}
