package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.CategoryEntity;
import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDto {

    private Long userNo;
    private Long parentCategoryNo;
    private Long categoryNo;
    private CategoryEntity parentCategory;
    private UserEntity userEntity;
    private String categoryName;
    private String categoryDesc;
    private LocalDateTime createdDate;
    private char deleteYn;


    @Data
    public static class RequestCategoryAddForm{
        private Long parentCategoryNo;
        private String categoryName;
        private String categoryDesc;
    }

    @Data
    public static class RequestCategoryModifyForm{
        private Long parentCategoryNo;
        private String categoryName;
        private String categoryDesc;
    }

    @Data
    public static class ResponseCategory{
        private Long categoryNo;
        private String categoryName;
        private String categoryDesc;
        private List<ChildCategory> childCategoryList;
        private LocalDateTime createdDate;
        private LocalDateTime lastModifiedDate;

        @Data
        public static class ChildCategory {
            private Long categoryNo;
            private Long parentCategoryNo;
            private String categoryName;
            private String categoryDesc;
            private LocalDateTime createdDate;
            private LocalDateTime lastModifiedDate;
        }
    }

    public CategoryEntity toEntity(){
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryName(categoryName)
                .categoryDesc(categoryDesc)
                .userInfo(userEntity)
                .parentCategory(parentCategory)
                .build();
        return categoryEntity;
    }

}
