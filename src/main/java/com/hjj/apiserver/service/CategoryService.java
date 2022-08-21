package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.AccountBookEntityJava;
import com.hjj.apiserver.domain.AccountBookUserEntityJava;
import com.hjj.apiserver.domain.CategoryEntityJava;
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
    public void addBasicCategory(AccountBookEntityJava accountBookEntity){
        String[] categoryName = {"식비","카페/간식", "유흥", "생활", "쇼핑", "교통", "통신/주거", "금융", "의료", "문화", "여행/숙박", "교육", "경조", "자녀/육아", "반려동물"};
        String[] categoryDesc = {"기본적인 식비", "식비를 제외한 카페, 디저트 등등", "술, 유흥", "생활하는데에 있어서 자잘한 금액", "온라인 쇼핑, 오프라인 쇼핑 등등", "버스/택시/지하철 요금", "인터넷비, 휴대폰비/월세, 전세자금등등","투자금, 주식,코인","병원비, 약국, 수술비", "영화, 뮤지컬, 여러 문환생활", "여행비, 숙박비 등등", "학원비, 온라인 강의", "경조사비", "육아비", "반려동물 케어 비용"};
        String baseIconUrl = "/images/";
        String[] categoryIcon = {"food.png", "coffee.png", "beer.png", "life.png", "shopping.png", "bus.png", "home.png", "money.png", "heart.png", "culture.png", "airplane.png", "education.png", "congratulation.png", "baby.png", "pet.png"};
        List<CategoryEntityJava> categoryEntityList = new ArrayList<>();
        for(int i = 0; i < categoryName.length; i++){
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setAccountBookEntity(accountBookEntity);
            categoryDto.setCategoryName(categoryName[i]);
            categoryDto.setCategoryDesc(categoryDesc[i]);
            categoryDto.setCategoryIcon(baseIconUrl + categoryIcon[i]);

            CategoryEntityJava categoryEntity = categoryDto.toEntity();
            categoryEntityList.add(categoryEntity);
        }
        categoryRepository.saveAll(categoryEntityList);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addCategory(CategoryDto categoryDto) throws UserNotFoundException {
        AccountBookEntityJava accountBookEntity = accountBookRepository.findAccountBookBySubQuery(categoryDto.getUserNo(), categoryDto.getAccountBookNo(), AccountBookUserEntityJava.AccountRole.OWNER).orElseThrow(UserNotFoundException::new);

        categoryDto.setAccountBookEntity(accountBookEntity);
        if(categoryDto.getParentCategoryNo() != null){
            categoryDto.setParentCategory(categoryRepository.getById(categoryDto.getParentCategoryNo()));
        }
        CategoryEntityJava categoryEntity = categoryDto.toEntity();
        categoryRepository.save(categoryEntity);
    }

    public CategoryDto.ResponseCategory findAllCategory(Long userNo, Long accountBookNo) throws Exception {
        List<CategoryEntityJava> categoryEntityList = categoryRepository.findEntityGraphBySubQuery(accountBookNo, userNo);
        CategoryDto.ResponseCategory responseCategory = new CategoryDto.ResponseCategory();
        List<CategoryDto.Category> categoryList = new ArrayList<>();
        categoryEntityList.stream().forEach(categoryEntity -> {
            CategoryDto.Category category = modelMapper.map(categoryEntity, CategoryDto.Category.class);
            if(categoryEntity.getParentCategory() == null){
                category.setAccountBookNo(accountBookNo);
                categoryList.add(category);
            }
        });
        responseCategory.setCategoryList(categoryList);
        responseCategory.setAccountRole(accountBookUserRepository.findByUserEntity_UserNoAndAccountBookEntity_AccountBookNo(userNo, accountBookNo).orElseThrow(Exception::new).getAccountRole());
        responseCategory.setAccountBookName(accountBookRepository.findById(accountBookNo).get().getAccountBookName());

        return responseCategory;
    }

    public CategoryDto.Category findCategory(Long categoryNo) throws Exception {
        CategoryEntityJava categoryEntity = categoryRepository.findById(categoryNo).orElseThrow(Exception::new);
        CategoryDto.Category category = modelMapper.map(categoryEntity, CategoryDto.Category.class);

        return category;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void modifyCategory(CategoryDto categoryDto) throws Exception {
        CategoryEntityJava updateCategory = categoryRepository.findByCategoryNoAndSubQuery(categoryDto.getCategoryNo(), categoryDto.getAccountBookNo(), categoryDto.getUserNo(), new ArrayList<>(Collections.singleton(AccountBookUserEntityJava.AccountRole.OWNER))).orElseThrow(UserNotFoundException::new);
        /* 최상위 카테고리를 하위 카테고리로 변경하려는 경우 또는 자기자신을 설정한경우 에러 처리 */
        if((updateCategory.getParentCategory() == null && categoryDto.getParentCategoryNo() != null) || updateCategory.getCategoryNo() == categoryDto.getParentCategoryNo()){
            throw new Exception();
        }
        if(categoryDto.getParentCategoryNo() != null){
            categoryDto.setParentCategory(categoryRepository.getById(categoryDto.getParentCategoryNo()));
        }
        updateCategory.updateCategory(categoryDto);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteCategory(Long categoryNo, Long accountBookNo, Long userNo) throws UserNotFoundException {
        CategoryEntityJava categoryEntity = categoryRepository.findByCategoryNoAndSubQuery(categoryNo, accountBookNo, userNo, new ArrayList<>(Collections.singleton(AccountBookUserEntityJava.AccountRole.OWNER))).orElseThrow(UserNotFoundException::new);
        categoryRepository.delete(categoryEntity);
    }
}
