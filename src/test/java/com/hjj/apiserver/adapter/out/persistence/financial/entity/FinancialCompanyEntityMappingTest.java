package com.hjj.apiserver.adapter.out.persistence.financial.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class FinancialCompanyEntityMappingTest {

    @Test
    void 회사_메타_컬럼은_배치_스키마_길이에_맞게_매핑한다() throws NoSuchFieldException {
        assertLength("financialCompanyCode", 20);
        assertLength("dclsMonth", 6);
        assertLength("hompUrl", 1024);
        assertLength("calTel", 100);
        assertLength("financialGroupType", 50);
        assertTextColumn("sourcePayload");
    }

    private void assertLength(String fieldName, int expectedLength) throws NoSuchFieldException {
        Field field = FinancialCompanyEntity.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.length()).isEqualTo(expectedLength);
    }

    private void assertTextColumn(String fieldName) throws NoSuchFieldException {
        Field field = FinancialCompanyEntity.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.columnDefinition()).isEqualTo("text");
    }
}
