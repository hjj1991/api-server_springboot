package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.CategoryEntity;
import com.hjj.apiserver.dto.CategoryDto;
import com.hjj.apiserver.repositroy.CategoryRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;



    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addCategory(CategoryDto categoryDto) throws UserNotFoundException {
        categoryDto.setUserEntity(userRepository.findByUserNo(categoryDto.getUserNo()).orElseThrow(UserNotFoundException::new));
        CategoryEntity categoryEntity = categoryDto.toEntity();
        categoryRepository.save(categoryEntity);
    }

    public List<CategoryDto.ResponseCategory> findCategory(Long userNo){
        List<CategoryEntity> categoryEntityList = categoryRepository.findEntityGraphByUserEntity_UserNoAndParentCategory_CategoryNo(userNo, null);
        return categoryEntityList.stream().map(p -> modelMapper.map(p, CategoryDto.ResponseCategory.class)).collect(Collectors.toList());

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void modifyCategory(CategoryDto categoryDto) throws Exception {
        CategoryEntity updateCategory =  categoryRepository.findByCategoryNoAndUserEntity_UserNo(categoryDto.getCategoryNo(), categoryDto.getUserNo()).orElseThrow(UserNotFoundException::new);
        /* 최상위 카테고리를 하위 카테고리로 변경하려는 경우 또는 자기자신을 설정한경우 에러 처리 */
        if((updateCategory.getParentCategory() == null && categoryDto.getParentCategoryNo() != null) || updateCategory.getCategoryNo() == categoryDto.getParentCategoryNo()){
            throw new Exception();
        }
        categoryDto.setParentCategory(categoryRepository.getById(categoryDto.getParentCategoryNo()));
        updateCategory.updateCategory(categoryDto);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteCategory(Long categoryNo, Long userNo) throws UserNotFoundException {
        CategoryEntity categoryEntity = categoryRepository.findByCategoryNoAndUserEntity_UserNo(categoryNo, userNo).orElseThrow(UserNotFoundException::new);
        categoryRepository.delete(categoryEntity);
    }
}
