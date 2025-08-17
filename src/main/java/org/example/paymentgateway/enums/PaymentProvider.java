package org.example.paymentgateway.enums;

public enum PaymentProvider {

    PAYSTACK("paystack"),
    FLUTTERWAVE("flutterWave");


    private final String name;

    PaymentProvider(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
