package com.hjj.apiserver.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse_Java<T> {
    private final boolean success;
    private final T response;
    private final ApiError_Java apiErrorJava;
    public ApiResponse_Java(boolean success, T response, ApiError_Java apiErrorJava) {
        this.success = success;
        this.response = response;
        this.apiErrorJava = apiErrorJava;
    }
}