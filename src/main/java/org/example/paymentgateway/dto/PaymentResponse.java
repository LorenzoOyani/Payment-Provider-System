package org.example.paymentgateway.dto;

import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private final boolean isSuccess;
    private final String paymentId;
    private final PaymentStatus status;
    private final String message;
    private final BigDecimal amount;
    private final String currency;
    private final String reference;
    private final Object metadata;
    private final String customerEmail;
    private final LocalDateTime created_at;
    private final LocalDateTime updated_at;
    private final PaymentProvider paymentProvider;

    public PaymentResponse(Builder builder) {
        this.isSuccess = builder.isSuccess;  // Fixed: removed static reference
        this.paymentId = builder.paymentId;
        this.status = builder.status;
        this.message = builder.message;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.reference = builder.reference;
        this.metadata = builder.metadata;
        this.customerEmail = builder.customerEmail;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
        this.paymentProvider = builder.paymentProvider;
    }

    // Add static builder method for MapStruct
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean isSuccess;  // Fixed: removed static modifier
        private String paymentId;
        private PaymentStatus status;
        private String message;
        private BigDecimal amount;
        private String currency;
        private String reference;
        private Object metadata;
        private String customerEmail;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
        private PaymentProvider paymentProvider;

        // Added missing isSuccess method
        public Builder isSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder metadata(Object metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        // Added missing methods for created_at and updated_at
        public Builder created_at(LocalDateTime created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder updated_at(LocalDateTime updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        // Fixed method name to follow camelCase convention
        public Builder paymentProvider(PaymentProvider paymentProvider) {
            this.paymentProvider = paymentProvider;
            return this;
        }

        public PaymentResponse build() {
            return new PaymentResponse(this);
        }
    }

    // Getters
    public boolean isSuccess() {
        return isSuccess;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReference() {
        return reference;
    }

    public Object getMetadata() {
        return metadata;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }
}