package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.AccountBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountBookRepository extends JpaRepository<AccountBookEntity, Long> {

}
