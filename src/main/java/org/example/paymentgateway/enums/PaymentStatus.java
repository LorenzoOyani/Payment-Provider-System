package org.example.paymentgateway.enums;

public enum PaymentStatus {

    PENDING("pending"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");


    private final String value;

    PaymentStatus(String value){
        this.value = value;

    };
    public String getValue() {
        return value;
    }

}
