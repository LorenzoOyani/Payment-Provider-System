package org.example.paymentgateway.dto;


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


}
