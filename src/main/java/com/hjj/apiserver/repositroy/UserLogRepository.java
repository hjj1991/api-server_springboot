package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLogEntity, Long> {
}
