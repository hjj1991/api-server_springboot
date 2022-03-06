package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByUserNo(Long userNo);
    Boolean existsUserEntityByUserId(String UserId);
    Boolean existsUserEntityByName(String name);

}
