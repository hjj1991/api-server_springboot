package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.CardEntityJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntityJava, Long> {
    List<CardEntityJava> findByUserEntity_UserNoAndDeleteYn(Long userNo, char deleteYn);
    Optional<CardEntityJava> findByCardNoAndUserEntity_UserNo(Long cardNo, Long userNo);
}
