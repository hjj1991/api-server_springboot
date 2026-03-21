package com.hjj.apiserver.adapter.out.persistence.financial.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class FinancialProductOptionEntityMappingTest {

    @Test
    void 예치_기간_컬럼은_smallint에_맞는_정수_타입으로_매핑한다() throws NoSuchFieldException {
        Field field = FinancialProductOptionEntity.class.getDeclaredField("depositPeriodMonths");
        Column column = field.getAnnotation(Column.class);

        assertThat(field.getType()).isEqualTo(int.class);
        assertThat(column).isNotNull();
        assertThat(column.columnDefinition()).isEqualTo("smallint");
    }

    @Test
    void 금리_컬럼은_numeric_8_5_정밀도에_맞게_매핑한다() throws NoSuchFieldException {
        Field baseInterestRate = FinancialProductOptionEntity.class.getDeclaredField("baseInterestRate");
        Field maximumInterestRate = FinancialProductOptionEntity.class.getDeclaredField("maximumInterestRate");

        assertPrecision(baseInterestRate);
        assertPrecision(maximumInterestRate);
    }

    @Test
    void 옵션_source_payload는_text로_매핑한다() throws NoSuchFieldException {
        Field field = FinancialProductOptionEntity.class.getDeclaredField("sourcePayload");
        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.columnDefinition()).isEqualTo("text");
    }

    private void assertPrecision(Field field) {
        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.precision()).isEqualTo(8);
        assertThat(column.scale()).isEqualTo(5);
    }
}
