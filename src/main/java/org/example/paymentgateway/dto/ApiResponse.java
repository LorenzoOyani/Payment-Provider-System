package org.example.paymentgateway.dto;

public class ApiResponse {
    private final String message;
    private final boolean success;


    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
