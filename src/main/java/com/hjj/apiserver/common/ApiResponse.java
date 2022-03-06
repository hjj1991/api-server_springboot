package com.hjj.apiserver.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T response;
    private final ApiError apiError;
    public ApiResponse(boolean success, T response, ApiError apiError) {
        this.success = success;
        this.response = response;
        this.apiError = apiError;
    }
}