package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.AccountBookEntity;
import com.hjj.apiserver.domain.AccountBookUserEntity;
import com.hjj.apiserver.domain.CategoryEntity;
import com.hjj.apiserver.dto.CategoryDto;
import com.hjj.apiserver.repositroy.AccountBookRepository;
import com.hjj.apiserver.repositroy.AccountBookUserRepository;
import com.hjj.apiserver.repositroy.CategoryRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final AccountBookUserRepository accountBookUserRepository;
    private final AccountBookRepository accountBookRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addBasicCategory(AccountBookEntity accountBookEntity){
        String[] categoryName = {"식비","카페/간식", "유흥", "생활", "쇼핑", "교통", "통신/주거", "금융", "의료", "문화", "여행/숙박", "교육", "경조", "자녀/육아", "반려동물"};
        String[] categoryDesc = {"기본적인 식비", "식비를 제외한 카페, 디저트 등등", "술, 유흥", "생활하는데에 있어서 자잘한 금액", "온라인 쇼핑, 오프라인 쇼핑 등등", "버스/택시/지하철 요금", "인터넷비, 휴대폰비/월세, 전세자금등등","투자금, 주식,코인","병원비, 약국, 수술비", "영화, 뮤지컬, 여러 문환생활", "여행비, 숙박비 등등", "학원비, 온라인 강의", "경조사비", "육아비", "반려동물 케어 비용"};
        String baseIconUrl = "/images/";
        String[] categoryIcon = {"food.png", "coffee.png", "beer.png", "life.png", "shopping.png", "bus.png", "home.png", "money.png", "heart.png", "culture.png", "airplane.png", "education.png", "congratulation.png", "baby.png", "pet.png"};
        List<CategoryEntity> categoryEntityList = new ArrayList<>();
        for(int i = 0; i < categoryName.length; i++){
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setAccountBookEntity(accountBookEntity);
            categoryDto.setCategoryName(categoryName[i]);
            categoryDto.setCategoryDesc(categoryDesc[i]);
            categoryDto.setCategoryIcon(baseIconUrl + categoryIcon[i]);

            CategoryEntity categoryEntity = categoryDto.toEntity();
            categoryEntityList.add(categoryEntity);
        }
        categoryRepository.saveAll(categoryEntityList);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addCategory(CategoryDto categoryDto) throws UserNotFoundException {
        AccountBookEntity accountBookEntity = accountBookRepository.findAccountBookBySubQuery(categoryDto.getUserNo(), categoryDto.getAccountBookNo(), AccountBookUserEntity.AccountRole.OWNER).orElseThrow(UserNotFoundException::new);

        categoryDto.setAccountBookEntity(accountBookEntity);
        categoryDto.setParentCategory(categoryRepository.getById(categoryDto.getParentCategoryNo()));
        CategoryEntity categoryEntity = categoryDto.toEntity();
        categoryRepository.save(categoryEntity);
    }

    public List<CategoryDto.ResponseCategory> findCategory(Long userNo, Long accountBookNo){
        List<CategoryEntity> categoryEntityList = categoryRepository.findEntityGraphBySubQuery(accountBookNo, userNo);
        List<CategoryDto.ResponseCategory> responseCategoryList = new ArrayList<>();
        categoryEntityList.stream().forEach(categoryEntity -> {
            CategoryDto.ResponseCategory responseCategory = modelMapper.map(categoryEntity, CategoryDto.ResponseCategory.class);
            if(categoryEntity.getParentCategory() == null){
                responseCategoryList.add(responseCategory);
            }


        });
        return responseCategoryList;

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void modifyCategory(CategoryDto categoryDto) throws Exception {
        CategoryEntity updateCategory = categoryRepository.findByCategoryNoAndSubQuery(categoryDto.getCategoryNo(), categoryDto.getAccountBookNo(), categoryDto.getUserNo(), new ArrayList<>(Collections.singleton(AccountBookUserEntity.AccountRole.OWNER))).orElseThrow(UserNotFoundException::new);
        /* 최상위 카테고리를 하위 카테고리로 변경하려는 경우 또는 자기자신을 설정한경우 에러 처리 */
        if((updateCategory.getParentCategory() == null && categoryDto.getParentCategoryNo() != null) || updateCategory.getCategoryNo() == categoryDto.getParentCategoryNo()){
            throw new Exception();
        }
        categoryDto.setParentCategory(categoryRepository.getById(categoryDto.getParentCategoryNo()));
        updateCategory.updateCategory(categoryDto);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteCategory(Long categoryNo, Long userNo, Long accountBookNo) throws UserNotFoundException {
        CategoryEntity categoryEntity = categoryRepository.findByCategoryNoAndSubQuery(categoryNo, userNo, accountBookNo, new ArrayList<>(Collections.singleton(AccountBookUserEntity.AccountRole.OWNER))).orElseThrow(UserNotFoundException::new);
        categoryRepository.delete(categoryEntity);
    }
}
