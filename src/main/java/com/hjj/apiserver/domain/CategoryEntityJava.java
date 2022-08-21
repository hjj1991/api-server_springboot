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
                @UniqueConstraint(columnNames = {"accountBookNo", "categoryName"})
        })
public class CategoryEntityJava extends BaseEntityJava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryNo;

    @Column(nullable = false)
    private String categoryName;

    @Column(columnDefinition = "varchar(5000) default ''")
    private String categoryDesc;

    @Column
    private String categoryIcon;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryEntity")
    @Builder.Default
    private List<PurchaseEntityJava> purchaseEntityList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountBookNo")
    private AccountBookEntityJava accountBookEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCategoryNo")
    private CategoryEntityJava parentCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CategoryEntityJava> childCategoryList = new ArrayList<>();

    public CategoryEntityJava updateCategory(CategoryDto categoryDto){
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
        if(categoryDto.getCategoryIcon() != null){
            this.categoryIcon = categoryDto.getCategoryIcon();
        }

        return this;
    }

    /* 연관관계 편의 메소드 */
    public void changeParentCategory(CategoryEntityJava parentCategory){
        if(this.parentCategory != null){
            this.parentCategory.getChildCategoryList().remove(this);
        }
        this.parentCategory = parentCategory;
        this.parentCategory.getChildCategoryList().add(this);
    }
}
