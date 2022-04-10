package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.CategoryEntity;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDto {

    private Long userNo;
    private Long accountBookNo;
    private Long parentCategoryNo;
    private Long categoryNo;
    private CategoryEntity parentCategory;
    private AccountBookEntity accountBookEntity;
    private String categoryName;
    private String categoryDesc;
    private String categoryIcon;
    private LocalDateTime createdDate;
    private char deleteYn;


    @Data
    public static class RequestCategoryAddForm{
        @NotNull
        private Long accountBookNo;
        private Long parentCategoryNo;
        @NotEmpty
        private String categoryName;
        private String categoryDesc;
        private String categoryIcon;
    }

    @Data
    public static class RequestCategoryModifyForm{
        @NotNull
        private Long accountBookNo;
        private Long parentCategoryNo;
        @NotEmpty
        private String categoryName;
        private String categoryDesc;
        private String categoryIcon;
    }

    @Data
    public static class RequestCategoryRemoveForm{
        @NotNull
        private Long accountBookNo;
    }

    @Data
    public static class PurchaseCategoryInfo{
        private Long parentCategoryNo;
        private Long categoryNo;
        private String parentCategoryName;
        private String categoryName;
        private String categoryDesc;
        private String categoryIcon;
    }

    @Data
    public static class Category{
        private Long accountBookNo;
        private Long categoryNo;
        private Long parentCategoryNo;
        private String categoryName;
        private String categoryDesc;
        private String categoryIcon;
        private List<ResponseCategory.ChildCategory> childCategoryList;
        private LocalDateTime createdDate;
        private LocalDateTime lastModifiedDate;
    }


    @Data
    public static class ResponseCategory{

        private List<Category> categoryList;
        private String accountBookName;
        private AccountBookUserEntity.AccountRole accountRole;

        @Data
        public static class ChildCategory {
            private Long accountBookNo;
            private Long categoryNo;
            private Long parentCategoryNo;
            private String categoryName;
            private String categoryDesc;
            private String categoryIcon;
            private LocalDateTime createdDate;
            private LocalDateTime lastModifiedDate;
        }
    }

    public CategoryEntity toEntity(){
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryName(categoryName)
                .categoryDesc(categoryDesc)
                .categoryIcon(categoryIcon)
                .accountBookInfo(accountBookEntity)
                .parentCategory(parentCategory)
                .build();
        return categoryEntity;
    }

}
