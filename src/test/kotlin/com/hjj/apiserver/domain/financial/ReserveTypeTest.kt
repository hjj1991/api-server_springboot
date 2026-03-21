package com.hjj.apiserver.domain.financial

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReserveTypeTest {
    @Test
    fun `알려진 코드는 정의된 enum으로 변환한다`() {
        assertThat(ReserveType.fromCode("F")).isEqualTo(ReserveType.FLEXIBLE)
        assertThat(ReserveType.fromCode("S")).isEqualTo(ReserveType.FIXED)
    }

    @Test
    fun `비어있거나 알 수 없는 코드는 null을 반환한다`() {
        assertThat(ReserveType.fromCode(null)).isNull()
        assertThat(ReserveType.fromCode("")).isNull()
        assertThat(ReserveType.fromCode("X")).isNull()
    }
}
