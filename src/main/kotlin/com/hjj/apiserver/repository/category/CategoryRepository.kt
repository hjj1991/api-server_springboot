package com.hjj.apiserver.repository.category

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface CategoryRepository: JpaRepository<Category, Long>, CategoryRepositoryCustom {

    @Query(
        "select c from Category c where c.categoryNo = :categoryNo and c.accountBook.accountBookNo = " +
                "(select abu.accountBook.accountBookNo from AccountBookUser abu " +
                "where abu.accountBook.accountBookNo = :accountBookNo and abu.user.userNo = :userNo and abu.accountRole in :accountRole)"
    )
    fun findByCategoryNoAndSubQuery(
        @Param("categoryNo") categoryNo: Long,
        @Param("accountBookNo") accountBookNo: Long,
        @Param("userNo") userNo: Long,
        @Param("accountRole") accountRole: List<AccountRole>
    ): Category?

}