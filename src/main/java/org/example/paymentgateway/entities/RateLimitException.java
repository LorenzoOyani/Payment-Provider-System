package org.example.paymentgateway.entities;

public class RateLimitException extends RuntimeException {
    private static final long serialVersionUID = 1012285931475769731L;

    public RateLimitException(String message) {
        super(message);
    }
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
