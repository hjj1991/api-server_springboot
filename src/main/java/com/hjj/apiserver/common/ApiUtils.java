package com.hjj.apiserver.common;

public class ApiUtils {
    public static <T> ApiResponse_Java<T> success(T response) {
        return new ApiResponse_Java<>(true, response, null);
    }


    public static <T> ApiResponse_Java<T> success() {
        return new ApiResponse_Java<>(true, null, null);
    }

    public static ApiResponse_Java<?> error(String message, ApiError_Java.ErrCode errCode){
        return new ApiResponse_Java<>(false, null, new ApiError_Java(message, errCode));
    }
}