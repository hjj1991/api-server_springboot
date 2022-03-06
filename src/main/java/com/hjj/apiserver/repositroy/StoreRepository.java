package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository  extends JpaRepository<StoreEntity, Long> {
}
