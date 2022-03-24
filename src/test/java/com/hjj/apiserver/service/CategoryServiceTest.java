package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.CategoryEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.CategoryDto;
import com.hjj.apiserver.repositroy.CategoryRepository;
import com.hjj.apiserver.repositroy.PurchaseRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired CategoryRepository categoryRepository;
    @Autowired CategoryService categoryService;
    @Autowired UserRepository userRepository;
    @Autowired PurchaseRepository purchaseRepository;

    @Test
    void addCategory(){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("식비");
        categoryDto.setCategoryDesc("커피값");
        categoryDto.setUserEntity(userRepository.getById(1L));

        CategoryEntity categoryEntity = categoryDto.toEntity();
        categoryRepository.save(categoryEntity);

        System.out.println("categoryEntity = " + categoryEntity);
    }

    @Test
    void addChildCateogry(){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("경조사");
        categoryDto.setCategoryDesc("커피값입니다.");
        categoryDto.setUserEntity(userRepository.getById(1L));
        categoryDto.setParentCategory(categoryRepository.getById(1L));
        CategoryEntity categoryEntity = categoryDto.toEntity();

        categoryRepository.save(categoryEntity);
    }

    @Test
    void findCategoryAll(){

        List<CategoryDto.ResponseCategory> categoryList =  categoryService.findCategory(1L);

        for (CategoryDto.ResponseCategory responseCategory : categoryList) {
            System.out.println("responseCategory.getCategoryName() = " + responseCategory.getCategoryName());
            System.out.println("responseCategory.getCategoryNo() = " + responseCategory.getCategoryNo());

            for (CategoryDto.ResponseCategory.ChildCategory childCategory : responseCategory.getChildCategoryList()) {
                System.out.println("childCategory.getCategoryName() = " + childCategory.getCategoryName());
                System.out.println("childCategory.getCategoryNo() = " + childCategory.getCategoryNo());
            }

            System.out.println("===========끝=============");
        }

    }

    @Test
    void updateCategory(){
        UserEntity userEntity = UserEntity.builder()
                .nickName("테스트캐릭터")
                .role(UserEntity.Role.USER)
                .createdDate(LocalDateTime.now()).build();

        userRepository.save(userEntity);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("경조사");
        categoryDto.setCategoryDesc("커피값입니다.");
        categoryDto.setUserEntity(userEntity);
        CategoryEntity categoryEntity = categoryDto.toEntity();

        categoryRepository.save(categoryEntity);
        categoryRepository.flush();

        CategoryEntity updateCategory =  categoryRepository.findByCategoryNoAndUserEntity_UserNo(categoryEntity.getCategoryNo(), userEntity.getUserNo()).get();
        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setCategoryName("파워붐붐");
        updateCategory.updateCategory(updateCategoryDto);
        categoryRepository.flush();


        Assertions.assertEquals(updateCategory.getCategoryName(), "파워붐붐");
    }

    @Test
    void deleteCategory(){
        UserEntity userEntity = UserEntity.builder()
                .nickName("테스트캐릭터")
                .role(UserEntity.Role.USER)
                .createdDate(LocalDateTime.now()).build();

        userRepository.save(userEntity);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("경조사");
        categoryDto.setCategoryDesc("커피값입니다.");
        categoryDto.setUserEntity(userEntity);
        CategoryEntity categoryEntity = categoryDto.toEntity();

        categoryRepository.save(categoryEntity);
        categoryRepository.flush();

        for(int i = 0; i < 20; i ++){
            PurchaseEntity purchaseEntity = PurchaseEntity.builder()
                    .categoryInfo(categoryEntity)
                    .userInfo(userEntity)
                    .purchaseDate(LocalDate.now())
                    .price(1000)
                    .purchaseType(PurchaseEntity.PurchaseType.OUTGOING)
                    .reason("커피묵느라")
                    .build();
            purchaseRepository.save(purchaseEntity);
            purchaseRepository.flush();
        }

        System.out.println(categoryRepository.existsByCategoryNoAndUserEntity_UserNo(categoryEntity.getCategoryNo(), userEntity.getUserNo()));

        purchaseRepository.deleteCategoryAllPurchaseEntityByCategoryNo(categoryEntity.getCategoryNo());
        categoryRepository.delete(categoryEntity);
        categoryRepository.flush();
    }


}