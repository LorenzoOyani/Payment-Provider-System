package org.example.paymentgateway.exception;

public class PaymentVerificationException extends RuntimeException {
    public PaymentVerificationException(String s) {
        super(s);
    }
    public PaymentVerificationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
