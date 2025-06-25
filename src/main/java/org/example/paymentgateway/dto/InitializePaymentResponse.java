package org.example.paymentgateway.dto;

import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;

public class InitializePaymentResponse {
//    private boolean isSuccess;
    private final String authorizationUrl;
    private final String reference;
    private final PaymentProvider provider;
    private final PaymentStatus status;
    private final String message;
    private final String access_code;
    private final FlutterWaveDetails flutterWaveDetails;
    private final PayStackDetails payStackDetails;

    public InitializePaymentResponse(Builder builder) {
//        this.isSuccess = builder.isSuccess;
        this.authorizationUrl = builder.authorizationUrl;
        this.reference = builder.reference;
        this.provider = builder.provider;
        this.flutterWaveDetails = builder.flutterWaveDetails;
        this.payStackDetails = builder.payStackDetails;
        this.status = builder.status;
        this.message = builder.message;
        access_code = builder.accessCode;
    }

    // Getters only (immutable object)
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public String getReference() {
        return reference;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public FlutterWaveDetails getFlutterWaveDetails() {
        return flutterWaveDetails;
    }

    public PayStackDetails getPayStackDetails() {
        return payStackDetails;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getAccess_code() {
        return access_code;
    }

    public static class Builder {
//        private boolean isSuccess;
        private String authorizationUrl;
        private String reference;
        private PaymentProvider provider;
        private PaymentStatus status;
        private String message;
        private String accessCode;
        private FlutterWaveDetails flutterWaveDetails;
        private PayStackDetails payStackDetails;

//        public Builder isSuccess(boolean isSuccess) {
//            this.isSuccess = isSuccess;
//            return this;
//        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;


        }

        public Builder accessCode(String accessCode) {
            this.accessCode = accessCode;
            return this;
        }

        public Builder authorizationUrl(String authorizationUrl) {
            this.authorizationUrl = authorizationUrl;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder provider(PaymentProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder flutterWaveDetails(FlutterWaveDetails flutterWaveDetails) {
            this.flutterWaveDetails = flutterWaveDetails;
            return this;
        }

        public Builder payStackDetails(PayStackDetails payStackDetails) {
            this.payStackDetails = payStackDetails;
            return this;
        }

        public InitializePaymentResponse build() {
            return new InitializePaymentResponse(
                    this
            );
        }

    }
}