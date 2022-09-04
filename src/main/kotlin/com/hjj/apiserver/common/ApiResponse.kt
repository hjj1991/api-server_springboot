package com.hjj.apiserver.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T>(
    val success: Boolean,
    val response: T?,
    val apiError: ApiError?,
) {
}