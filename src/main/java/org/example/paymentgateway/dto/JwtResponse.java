package org.example.paymentgateway.dto;

public class JwtResponse {
    private final String accessToken;
    private static final String tokenType = "Bearer";

    public JwtResponse(String userToken) {
        this.accessToken = userToken;

    }

    public String getAccessToken() {
        return tokenType + accessToken;
    }
}
