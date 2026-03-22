package com.hjj.apiserver.adapter.out.persistence.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class AuthEntityMappingTest {

    @Test
    void 사용자엔티티는_이메일암호문과_조회해시를_분리한다() throws NoSuchFieldException {
        Field emailCiphertext = UserEntity.class.getDeclaredField("emailCiphertext");
        Field emailLookupHash = UserEntity.class.getDeclaredField("emailLookupHash");
        Field emailVerifiedAt = UserEntity.class.getDeclaredField("emailVerifiedAt");
        Field tokenVersion = UserEntity.class.getDeclaredField("tokenVersion");
        Field status = UserEntity.class.getDeclaredField("status");

        assertThat(emailCiphertext.getAnnotation(Column.class).columnDefinition()).isEqualTo("text");
        assertThat(emailLookupHash.getAnnotation(Column.class).length()).isEqualTo(64);
        assertThat(emailVerifiedAt.getType()).isEqualTo(OffsetDateTime.class);
        assertThat(emailVerifiedAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(tokenVersion.getType()).isEqualTo(long.class);
        assertThat(status.getAnnotation(Column.class).length()).isEqualTo(32);
    }

    @Test
    void 인증식별자엔티티는_provider_hash와_시각을_명시한다() throws NoSuchFieldException {
        Field providerType = AuthIdentityEntity.class.getDeclaredField("providerType");
        Field loginId = AuthIdentityEntity.class.getDeclaredField("loginId");
        Field providerSubjectHash = AuthIdentityEntity.class.getDeclaredField("providerSubjectHash");
        Field linkedAt = AuthIdentityEntity.class.getDeclaredField("linkedAt");
        Field lastLoginAt = AuthIdentityEntity.class.getDeclaredField("lastLoginAt");

        assertThat(providerType.getAnnotation(Column.class).length()).isEqualTo(20);
        assertThat(loginId.getAnnotation(Column.class).length()).isEqualTo(50);
        assertThat(providerSubjectHash.getAnnotation(Column.class).length()).isEqualTo(64);
        assertThat(linkedAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(lastLoginAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
    }

    @Test
    void 로컬인증과_삭제요청은_보안필드와_시각필드를_명시한다() throws NoSuchFieldException {
        Field passwordHash = LocalCredentialEntity.class.getDeclaredField("passwordHash");
        Field passwordAlgo = LocalCredentialEntity.class.getDeclaredField("passwordAlgo");
        Field lockedUntil = LocalCredentialEntity.class.getDeclaredField("lockedUntil");
        Field scheduledPurgeAt = UserDeletionRequestEntity.class.getDeclaredField("scheduledPurgeAt");
        Field cancelledAt = UserDeletionRequestEntity.class.getDeclaredField("cancelledAt");
        Field purgedAt = UserDeletionRequestEntity.class.getDeclaredField("purgedAt");
        Field status = UserDeletionRequestEntity.class.getDeclaredField("status");

        assertThat(passwordHash.getAnnotation(Column.class).length()).isEqualTo(255);
        assertThat(passwordAlgo.getAnnotation(Column.class).length()).isEqualTo(50);
        assertThat(lockedUntil.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(scheduledPurgeAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(cancelledAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(purgedAt.getAnnotation(Column.class).columnDefinition()).isEqualTo("timestamptz");
        assertThat(status.getAnnotation(Column.class).length()).isEqualTo(20);
    }
}
