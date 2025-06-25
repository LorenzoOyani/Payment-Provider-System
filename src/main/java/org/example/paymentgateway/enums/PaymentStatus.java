package org.example.paymentgateway.enums;

public enum PaymentStatus {

    PENDING("pending"),
    SUCCESS("success"),
    FAILURE("failure");


    private final String value;

    PaymentStatus(String value){
        this.value = value;

    };

}
