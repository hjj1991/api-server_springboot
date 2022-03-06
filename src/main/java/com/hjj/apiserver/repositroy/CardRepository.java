package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    List<CardEntity> findByUserInfo_UserNoAndDeleteYn(Long userNo, char deleteYn);
    Optional<CardEntity> findByCardNoAndUserInfo_UserNo(Long cardNo, Long userNo);
}
