package org.example.paymentgateway.exception;

public class WebhookException extends RuntimeException {

    public WebhookException(String message) {
        super(message);
    }
    public WebhookException(String message, Throwable cause) {
        super(message, cause);

    }
}
