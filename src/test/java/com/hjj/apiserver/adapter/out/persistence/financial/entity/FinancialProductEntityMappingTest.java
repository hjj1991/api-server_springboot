package com.hjj.apiserver.adapter.out.persistence.financial.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import java.lang.reflect.Field;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

class FinancialProductEntityMappingTest {

    @Test
    void 텍스트_컬럼은_lob_대신_text_타입으로_매핑한다() throws NoSuchFieldException {
        assertTextColumn("joinWay");
        assertTextColumn("postMaturityInterestRate");
        assertTextColumn("specialCondition");
        assertTextColumn("joinMember");
        assertTextColumn("additionalNotes");
    }

    @Test
    void 임베딩_컬럼은_pgvector로_매핑한다() throws NoSuchFieldException {
        Field field = FinancialProductEntity.class.getDeclaredField("embeddingVector");

        JdbcTypeCode jdbcTypeCode = field.getAnnotation(JdbcTypeCode.class);
        Column column = field.getAnnotation(Column.class);

        assertThat(field.getAnnotation(Lob.class)).isNull();
        assertThat(jdbcTypeCode).isNotNull();
        assertThat(jdbcTypeCode.value()).isEqualTo(SqlTypes.VECTOR);
        assertThat(column).isNotNull();
        assertThat(column.columnDefinition()).isEqualTo("vector(768)");
    }

    private void assertTextColumn(String fieldName) throws NoSuchFieldException {
        Field field = FinancialProductEntity.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertThat(field.getAnnotation(Lob.class)).isNull();
        assertThat(column).isNotNull();
        assertThat(column.columnDefinition()).isEqualTo("text");
    }
}
