package com.hjj.apiserver.common;

public class ApiUtils {
    public static <T>ApiResponse<T> success(T response) {
        return new ApiResponse<>(true, response, null);
    }


    public static <T>ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<?> error(String message, ApiError.ErrCode errCode){
        return new ApiResponse<>(false, null, new ApiError(message, errCode));
    }
}