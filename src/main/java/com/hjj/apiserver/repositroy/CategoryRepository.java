package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.CategoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryEntity, Long> {

    @EntityGraph(attributePaths = {"parentCategory"})
    List<CategoryEntity> findEntityGraphByUserInfo_UserNoAndParentCategory_CategoryNo(Long userNo, Long parentCategoryNo);
    Optional<CategoryEntity> findByCategoryNoAndUserInfo_UserNo(Long categoryNo, Long userNo);
    Boolean existsByCategoryNoAndUserInfo_UserNo(Long categoryNo, Long userNo);
}
