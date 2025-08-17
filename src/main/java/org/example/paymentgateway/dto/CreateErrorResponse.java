package org.example.paymentgateway.dto;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record CreateErrorResponse<T>(T error) {

    public static <T> CreateErrorResponse<T> create(T error) {
        return new CreateErrorResponse<>(error);
    }

    public static <T> CreateErrorResponse<T> create(Map<String, T> errorResponse, T defaultError) {

        if (errorResponse == null || !errorResponse.containsKey("error")) {
            return new CreateErrorResponse<T>(defaultError);
        }
        return new CreateErrorResponse<T>(errorResponse.get("error"));
    }

    public static <T> ApiResponse<T> createApiResponse(T error, String errorDetails) {
        if (errorDetails == null || errorDetails.trim().isEmpty()) {
            errorDetails = "An error occurred!";
        }
        return ApiResponse.error(error, errorDetails);
    }

    public static <T> ApiResponse<T> createApiResponse(T error, String errorDetails, String errorCode) {
        if (errorDetails == null || errorDetails.trim().isEmpty()) {
            errorDetails = "An error occurred!";
        }
        return ApiResponse.<T>builder()
                .success(false)
                .message(errorDetails)
                .errorCode(errorCode)
                .errorObject(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
