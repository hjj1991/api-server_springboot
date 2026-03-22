package com.hjj.apiserver.adapter.out.persistence.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class AuthEntityIndexMetadataTest {

    @Test
    void 사용자엔티티는_규칙적인_인덱스와_유니크명을_가진다() {
        assertIndexes(UserEntity.class, Map.of("ix_users__status", "status"));
        assertUniqueConstraints(UserEntity.class, Map.of("uq_users__email_lookup_hash", "email_lookup_hash"));
    }

    @Test
    void 인증식별자엔티티는_규칙적인_인덱스와_유니크명을_가진다() {
        assertIndexes(
                AuthIdentityEntity.class,
                Map.of("ix_auth_identities__user_id_provider_type", "user_id,provider_type"));
        assertUniqueConstraints(
                AuthIdentityEntity.class,
                Map.of(
                        "uq_auth_identities__provider_type_subject_hash",
                        "provider_type,provider_subject_hash",
                        "uq_auth_identities__provider_type_login_id",
                        "provider_type,login_id"));
    }

    @Test
    void 로컬인증정보와_역할엔티티는_규칙적인_유니크명을_가진다() {
        assertUniqueConstraints(
                LocalCredentialEntity.class,
                Map.of("uq_local_credentials__user_id", "user_id"));
        assertUniqueConstraints(RoleEntity.class, Map.of("uq_roles__role_name", "role_name"));
    }

    @Test
    void 사용자역할과_삭제요청엔티티는_규칙적인_인덱스명을_가진다() {
        assertIndexes(UserRoleEntity.class, Map.of("ix_user_roles__role_id", "role_id"));
        assertUniqueConstraints(
                UserRoleEntity.class,
                Map.of("uq_user_roles__user_id_role_id", "user_id,role_id"));
        assertIndexes(
                UserDeletionRequestEntity.class,
                Map.of(
                        "ix_user_deletion_requests__user_id_requested_at",
                        "user_id,requested_at",
                        "ix_user_deletion_requests__status_scheduled_purge_at",
                        "status,scheduled_purge_at"));
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

    private void assertUniqueConstraints(Class<?> entityClass, Map<String, String> expectedConstraints) {
        Table table = entityClass.getAnnotation(Table.class);

        assertThat(table).isNotNull();
        Set<String> actualNames =
                Arrays.stream(table.uniqueConstraints()).map(UniqueConstraint::name).collect(Collectors.toSet());
        assertThat(actualNames).containsExactlyInAnyOrderElementsOf(expectedConstraints.keySet());
        Map<String, String> actualConstraints =
                Arrays.stream(table.uniqueConstraints())
                        .collect(Collectors.toMap(
                                UniqueConstraint::name,
                                it -> String.join(",", it.columnNames())));
        assertThat(actualConstraints).containsExactlyInAnyOrderEntriesOf(expectedConstraints);
    }
}
