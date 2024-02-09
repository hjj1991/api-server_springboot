package com.hjj.apiserver.utils

import org.mockito.Mockito

class MockitoTestUtil {

    companion object {

        fun <T> any(clazz: Class<T>): T {
            Mockito.any<T>(clazz)
            return null as T
        }
    }
}