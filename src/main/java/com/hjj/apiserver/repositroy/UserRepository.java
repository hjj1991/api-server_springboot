package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByProviderAndProviderId(UserEntity.Provider provider, String providerId);
    Boolean existsUserEntityByProviderIdAndProviderAndDeleteYn(String providerId, UserEntity.Provider provider, char deletedYn);
    Boolean existsUserEntityByUserId(String UserId);
    Boolean existsUserEntityByNickNameAndUserNoNot(String nickName, Long userNo);
    Optional<UserEntity> findByRefreshToken(String refreshToken);

}
