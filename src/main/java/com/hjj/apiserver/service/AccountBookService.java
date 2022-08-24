package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.AccountBookEntityJava;
import com.hjj.apiserver.domain.PurchaseEntityJava;
import com.hjj.apiserver.dto.AccountBookDto;
import com.hjj.apiserver.dto.AccountBookUserDto;
import com.hjj.apiserver.repositroy.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Transactional(readOnly = true)
@AllArgsConstructor
public class AccountBookService {

    private final AccountBookUserRepository accountBookUserRepository;
    private final AccountBookRepository accountBookRepository;
    private final CategoryService categoryService;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final CardService cardService;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addAccountBook(UserEntity user, AccountBookDto accountBookDto) throws UserNotFoundException {

        AccountBookEntityJava accountBookEntity = accountBookDto.toEntity();
        accountBookRepository.save(accountBookEntity);

        /* 가계부 생성후 유저와 매핑 해주어야 한다. (기본 권한은 USER이므로 OWNER를 넣는다. */
        AccountBookUserDto accountBookUserDto = new AccountBookUserDto();
        accountBookUserDto.setAccountBookEntity(accountBookEntity);
        accountBookUserDto.setAccountRole(AccountBookUserEntityJava.AccountRole.OWNER);
        accountBookUserDto.setUserEntity(user);
        accountBookUserDto.setBackGroundColor(accountBookDto.getBackGroundColor());
        accountBookUserDto.setColor(accountBookDto.getColor());
        AccountBookUserEntityJava accountBookUserEntity = accountBookUserDto.toEntity();
        accountBookUserRepository.save(accountBookUserEntity);

        categoryService.addBasicCategory(accountBookEntity);
    }

    public AccountBookDto.ResponseAccountBookDetail findAccountBookDetail(AccountBookDto accountBookDto) throws Exception {
        AccountBookDto.ResponseAccountBookDetail responseAccountBookDetail = new AccountBookDto.ResponseAccountBookDetail();
        responseAccountBookDetail.setAccountBookName(accountBookRepository.getById(accountBookDto.getAccountBookNo()).getAccountBookName());
        responseAccountBookDetail.setCardList(cardService.selectCardList(accountBookDto.getUserNo()));
        responseAccountBookDetail.setCategoryList(categoryService.findAllCategory(accountBookDto.getUserNo(), accountBookDto.getAccountBookNo()));

        return responseAccountBookDetail;
    }


    public List<AccountBookDto.ResponseAccountBookFindAll> findAllAccountBook(AccountBookDto accountBookDto) {
        /* userNo으로 가계부 <-> 유저 매핑 테이블 조회 */
        List<AccountBookUserEntityJava> accountBookUserEntityList =  accountBookUserRepository.findEntityGraphByUserEntity_userNo(accountBookDto.getUserNo());
        List<Long> accountBookNoList = new ArrayList<>();
        List<AccountBookDto.ResponseAccountBookFindAll> responseAccountBookFindAllList = new ArrayList<>();

        accountBookUserEntityList.stream().forEach(accountBookUserEntity -> accountBookNoList.add(accountBookUserEntity.getAccountBookEntity().getAccountBookNo()));

        List<PurchaseEntityJava> purchaseEntityList = purchaseRepository.findAllEntityGraphByPurchaseDateBetweenAndUserEntity_UserNoOrderByPurchaseDateDesc(accountBookDto.getStartDate(), accountBookDto.getEndDate(), accountBookDto.getUserNo());
        List<AccountBookUserEntityJava> tempAccountBookUserList = accountBookUserRepository.findEntityGraphByAccountBookEntity_accountBookNoIn(accountBookNoList);


        accountBookUserEntityList.stream().forEach(accountBookUserEntity -> {
            Long accountBookNo = accountBookUserEntity.getAccountBookEntity().getAccountBookNo();
            AccountBookDto.ResponseAccountBookFindAll responseAccountBookFindAll = new AccountBookDto.ResponseAccountBookFindAll();
            responseAccountBookFindAll.setAccountBookNo(accountBookNo);
            responseAccountBookFindAll.setAccountBookName(accountBookUserEntity.getAccountBookEntity().getAccountBookName());
            responseAccountBookFindAll.setAccountBookDesc(accountBookUserEntity.getAccountBookEntity().getAccountBookDesc());
            responseAccountBookFindAll.setAccountRole(accountBookUserEntity.getAccountRole());
            responseAccountBookFindAll.setBackGroundColor(accountBookUserEntity.getBackGroundColor());
            responseAccountBookFindAll.setColor(accountBookUserEntity.getColor());

            int totalIncomeAmount = 0;
            int totalOutgoingAmount = 0;
            for (PurchaseEntityJava purchaseEntity : purchaseEntityList) {
                if(purchaseEntity.getPurchaseType().equals(PurchaseEntityJava.PurchaseType.INCOME) && purchaseEntity.getAccountBookEntity().getAccountBookNo() == accountBookNo){
                    totalIncomeAmount += purchaseEntity.getPrice();
                }else if(purchaseEntity.getPurchaseType().equals(PurchaseEntityJava.PurchaseType.OUTGOING) && purchaseEntity.getAccountBookEntity().getAccountBookNo() == accountBookNo){
                    totalOutgoingAmount += purchaseEntity.getPrice();
                }

            }
            responseAccountBookFindAll.setTotalIncomeAmount(totalIncomeAmount);
            responseAccountBookFindAll.setTotalOutgoingAmount(totalOutgoingAmount);

            List<AccountBookDto.ResponseAccountBookFindAll.JoinedUser> joinedUserList = new ArrayList<>();

            tempAccountBookUserList.stream().forEach(tempAccountBookEntity ->{
                if(accountBookNo == tempAccountBookEntity.getAccountBookEntity().getAccountBookNo()){
                    AccountBookDto.ResponseAccountBookFindAll.JoinedUser joinedUser = new AccountBookDto.ResponseAccountBookFindAll.JoinedUser();
                    joinedUser.setUserNo(tempAccountBookEntity.getUserEntity().getUserNo());
                    joinedUser.setNickName(tempAccountBookEntity.getUserEntity().getNickName());
                    joinedUser.setPicture(tempAccountBookEntity.getUserEntity().getPicture());
                    joinedUserList.add(joinedUser);
                }
            });

            responseAccountBookFindAll.setJoinedUserList(joinedUserList);
            responseAccountBookFindAllList.add(responseAccountBookFindAll);

        });

        return responseAccountBookFindAllList;
    }
}
