package com.hjj.apiserver.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T>(
    private val success: Boolean,
    private val response: T?,
    private val apiError: ApiError?,
) {
}