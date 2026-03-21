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
}
