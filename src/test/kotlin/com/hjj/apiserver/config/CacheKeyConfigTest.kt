package com.hjj.apiserver.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CacheKeyConfigTest {
    private val keyGenerator = CacheKeyConfig().paramsLocalDateCacheKey()
    private val target = DummyTarget()
    private val findMethod = DummyTarget::class.java.getDeclaredMethod("find", Long::class.javaPrimitiveType)

    @Test
    fun `같은 날 다른 파라미터는 다른 캐시 키를 만든다`() {
        val firstKey = keyGenerator.generate(target, findMethod, 1L)
        val secondKey = keyGenerator.generate(target, findMethod, 2L)

        assertThat(firstKey).isNotEqualTo(secondKey)
        assertThat(firstKey.toString()).endsWith("|1")
        assertThat(secondKey.toString()).endsWith("|2")
    }

    private class DummyTarget {
        @Suppress("unused")
        fun find(financialProductId: Long) = financialProductId
    }
}
