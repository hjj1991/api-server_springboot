package com.hjj.apiserver.util

class CommonUtils {

    companion object {
        /* 페이징 slice */
        fun <T> getSlicePageResult(result: List<T>, limit: Int): List<T> {
            val returnValue: MutableList<T> = ArrayList()
            var cnt = 0
            for (obj in result) {
                if (cnt == limit) {
                    break
                }
                returnValue.add(obj)
                cnt++
            }
            return returnValue
        }
    }
}
