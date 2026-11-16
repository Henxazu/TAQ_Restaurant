package com.university.restaurant.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * [Shared - Locked Week 1] Standard JSON wrapper for every /api/v1/** endpoint:
 * <pre>{ "success": true, "message": "...", "data": { ... } }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}