package com.hjj.apiserver.adapter.out.persistence.financial.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class FinancialEntityIndexMetadataTest {

    @Test
    void 회사엔티티는_규칙적인_인덱스명을_가진다() {
        assertIndexes(
                FinancialCompanyEntity.class,
                Map.of(
                        "ix_financial_company__financial_company_code",
                        "financial_company_code",
                        "ix_financial_company__company_name",
                        "company_name"));
    }

    @Test
    void 상품엔티티는_규칙적인_인덱스명을_가진다() {
        assertIndexes(
                FinancialProductEntity.class,
                Map.of(
                        "ix_financial_product__financial_company_id",
                        "financial_company_id",
                        "ix_financial_product__financial_product_code",
                        "financial_product_code",
                        "ix_financial_product__financial_product_name",
                        "financial_product_name",
                        "ix_financial_product__financial_product_type_status_last_seen_at",
                        "financial_product_type,status,last_seen_at"));
    }

    @Test
    void 옵션엔티티는_규칙적인_인덱스명을_가진다() {
        assertIndexes(
                FinancialProductOptionEntity.class,
                Map.of(
                        "ix_financial_product_option__financial_product_id",
                        "financial_product_id",
                        "ix_financial_product_option__financial_product_id_deposit_period_months_interest_rate_type_reserve_type",
                        "financial_product_id,deposit_period_months,interest_rate_type,reserve_type"));
    }

    private void assertIndexes(Class<?> entityClass, Map<String, String> expectedIndexes) {
        Table table = entityClass.getAnnotation(Table.class);

        assertThat(table).isNotNull();
        Set<String> actualNames = Arrays.stream(table.indexes()).map(Index::name).collect(Collectors.toSet());
        assertThat(actualNames).containsExactlyInAnyOrderElementsOf(expectedIndexes.keySet());
        Map<String, String> actualIndexes =
                Arrays.stream(table.indexes())
                        .collect(Collectors.toMap(Index::name, Index::columnList));
        assertThat(actualIndexes).containsExactlyInAnyOrderEntriesOf(expectedIndexes);
    }
}
