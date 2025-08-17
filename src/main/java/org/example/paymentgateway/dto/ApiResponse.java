package org.example.paymentgateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_details")
    private String errorDetails;

    @JsonProperty("error_object")
    private T errorObject;

    // Default constructor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for successful responses
    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor for error responses
    public ApiResponse(boolean success, String message, String errorCode, String errorDetails) {
        this();
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    // Builder pattern for fluent API
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public T getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(T errorObject) {
        this.errorObject = errorObject;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", requestId='" + requestId + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorDetails='" + errorDetails + '\'' +
                ", errorObject=" + errorObject +
                '}';
    }


    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;
        private String requestId;
        private String errorCode;
        private String errorDetails;
        private T errorObject;

        public ApiResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponseBuilder<T> requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ApiResponseBuilder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ApiResponseBuilder<T> errorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public ApiResponseBuilder<T> errorObject(T errorObject) {
            this.errorObject = errorObject;
            return this;
        }

        public ApiResponse<T> build() {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = this.success;
            response.message = this.message;
            response.data = this.data;
            response.timestamp = this.timestamp != null ? this.timestamp : LocalDateTime.now();
            response.requestId = this.requestId;
            response.errorCode = this.errorCode;
            response.errorDetails = this.errorDetails;
            response.errorObject = this.errorObject;
            return response;
        }
    }

    // Static utility methods for common response patterns
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static <T> ApiResponse<T> error(T errorObject, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorObject(errorObject)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, String errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .build();
    }
}