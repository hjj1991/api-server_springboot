package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.AccountBookDto;
import com.hjj.apiserver.dto.AccountBookUserDto;
import com.hjj.apiserver.repositroy.AccountBookRepository;
import com.hjj.apiserver.repositroy.AccountBookUserRepository;
import com.hjj.apiserver.repositroy.PurchaseRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AccountBookService {

    private final AccountBookUserRepository accountBookUserRepository;
    private final AccountBookRepository accountBookRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addAccountBook(AccountBookDto accountBookDto) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByUserNo(accountBookDto.getUserNo()).orElseThrow(UserNotFoundException::new);

        AccountBookEntity accountBookEntity = accountBookDto.toEntity();
        accountBookRepository.save(accountBookEntity);

        /* 가계부 생성후 유저와 매핑 해주어야 한다. (기본 권한은 USER이므로 OWNER를 넣는다. */
        AccountBookUserDto accountBookUserDto = new AccountBookUserDto();
        accountBookUserDto.setAccountBookInfo(accountBookEntity);
        accountBookUserDto.setAccountRole(AccountBookUserEntity.AccountRole.OWNER);
        accountBookUserDto.setUserInfo(userEntity);
        AccountBookUserEntity accountBookUserEntity = accountBookUserDto.toEntity();
        accountBookUserRepository.save(accountBookUserEntity);
    }


    public List<AccountBookDto.ResponseAccountBookFindAll> findAllAccountBook(AccountBookDto accountBookDto) {
        List<AccountBookUserEntity> accountBookUserEntityList =  accountBookUserRepository.findEntityGraphByUserInfo_userNo(accountBookDto.getUserNo());
        List<Long> accountBookNoList = new ArrayList<>();
        accountBookUserEntityList.stream().forEach(accountBookUserEntity -> accountBookNoList.add(accountBookUserEntity.getAccountBookInfo().getAccountBookNo()));
        List<AccountBookDto.ResponseAccountBookFindAll> responseAccountBookFindAllList = new ArrayList<>();


        List<PurchaseEntity> purchaseEntityList = purchaseRepository.findAllEntityGraphByPurchaseDateBetweenAndUserInfo_UserNoAndDeleteYnOrderByPurchaseDateDesc(accountBookDto.getStartDate(), accountBookDto.getEndDate(), accountBookDto.getUserNo(), 'N');
        List<AccountBookUserEntity> tempAccountBookUserList = accountBookUserRepository.findEntityGraphByAccountBookInfo_accountBookNoIn(accountBookNoList);


        accountBookUserEntityList.stream().forEach(accountBookUserEntity -> {
            Long accountBookNo = accountBookUserEntity.getAccountBookInfo().getAccountBookNo();
            AccountBookDto.ResponseAccountBookFindAll responseAccountBookFindAll = new AccountBookDto.ResponseAccountBookFindAll();
            responseAccountBookFindAll.setAccountBookNo(accountBookNo);
            responseAccountBookFindAll.setAccountBookName(accountBookUserEntity.getAccountBookInfo().getAccountBookName());
            responseAccountBookFindAll.setAccountBookDesc(accountBookUserEntity.getAccountBookInfo().getAccountBookDesc());

            int totalIncomeAmount = 0;
            int totalOutgoingAmount = 0;
            for (PurchaseEntity purchaseEntity : purchaseEntityList) {
                if(purchaseEntity.getPurchaseType().equals(PurchaseEntity.PurchaseType.INCOME) && purchaseEntity.getAccountBookInfo().getAccountBookNo() == accountBookNo){
                    totalIncomeAmount += purchaseEntity.getPrice();
                }else if(purchaseEntity.getPurchaseType().equals(PurchaseEntity.PurchaseType.OUTGOING) && purchaseEntity.getAccountBookInfo().getAccountBookNo() == accountBookNo){
                    totalOutgoingAmount += purchaseEntity.getPrice();
                }

            }
            responseAccountBookFindAll.setTotalIncomeAmount(totalIncomeAmount);
            responseAccountBookFindAll.setTotalOutgoingAmount(totalOutgoingAmount);

            List<AccountBookDto.ResponseAccountBookFindAll.JoinedUser> joinedUserList = new ArrayList<>();

            tempAccountBookUserList.stream().forEach(tempAccountBookEntity ->{
                if(accountBookNo == tempAccountBookEntity.getAccountBookInfo().getAccountBookNo()){
                    AccountBookDto.ResponseAccountBookFindAll.JoinedUser joinedUser = new AccountBookDto.ResponseAccountBookFindAll.JoinedUser();
                    joinedUser.setNickName(tempAccountBookEntity.getUserInfo().getNickName());
                    joinedUser.setPicture(tempAccountBookEntity.getUserInfo().getPicture());
                    joinedUserList.add(joinedUser);
                }
            });

            responseAccountBookFindAll.setJoinedUserList(joinedUserList);
            responseAccountBookFindAllList.add(responseAccountBookFindAll);

        });

        return responseAccountBookFindAllList;
    }
}
