package com.hjj.apiserver.domain;

import com.hjj.apiserver.dto.CategoryDto;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "tb_category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"categoryNo", "categoryName"})
        })
public class CategoryEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryNo;

    @Column(nullable = false)
    private String categoryName;

    @Column(columnDefinition = "varchar(5000) default ''")
    private String categoryDesc;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryInfo")
    @Builder.Default
    private List<PurchaseEntity> purchaseEntityList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userNo")
    private UserEntity userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCategoryNo")
    private CategoryEntity parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CategoryEntity> childCategoryList = new ArrayList<>();

    public CategoryEntity updateCategory(CategoryDto categoryDto){
        if(categoryDto.getCategoryName() != null){
            this.categoryName = categoryDto.getCategoryName();
        }
        if(categoryDto.getCategoryDesc() != null){
            this.categoryDesc = categoryDto.getCategoryDesc();
        }
        if(categoryDto.getParentCategory() != null){
            this.parentCategory = categoryDto.getParentCategory();
            changeParentCategory(categoryDto.getParentCategory());
        }

        return this;
    }

    /* 연관관계 편의 메소드 */
    public void changeParentCategory(CategoryEntity parentCategory){
        if(this.parentCategory != null){
            this.parentCategory.getChildCategoryList().remove(this);
        }
        this.parentCategory = parentCategory;
        this.parentCategory.getChildCategoryList().add(this);
    }
}
