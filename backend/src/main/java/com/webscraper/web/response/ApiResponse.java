package com.webscraper.web.response;

import java.time.Instant;

/**
 * Standard API response envelope.
 * Provides consistent structure for all responses.
 */
public record ApiResponse<T>(
    boolean success,
    T data,
    ErrorInfo error,
    Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message, null), Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message, details), Instant.now());
    }

    public record ErrorInfo(String code, String message, Object details) {}
}
