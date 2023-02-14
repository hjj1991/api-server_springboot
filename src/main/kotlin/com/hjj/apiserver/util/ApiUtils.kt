package com.hjj.apiserver.util

import com.hjj.apiserver.common.ApiError
import com.hjj.apiserver.common.ErrCode

class ApiUtils {
    companion object {
        fun <T> success(response: T):ApiResponse<T>{
            return ApiResponse(true, response, null)
        }
        fun success():ApiResponse<*>{
            return ApiResponse(true, null, null)
        }

        fun error(errorCode: ErrCode):ApiResponse<*>{
            return ApiResponse(false, null, ApiError(errorCode))
        }
    }
}