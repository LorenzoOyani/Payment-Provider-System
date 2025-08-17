package org.example.paymentgateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.paymentgateway.enums.PaymentProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentVerificationResponse {
    private boolean verified;
    private String status;
    private String message;
    private PaymentResponse paymentResponse;

    public boolean isVerified() {
        return verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public static class PaymentVerificationData {
        private final String reference;
        private final BigDecimal amount;
        private final String currency;
        private final String status;
        private PaymentResponse paymentResponse;

        @JsonProperty("gateway_response")
        private final String gatewayResponse;

        @JsonProperty("paid_at")
        private final LocalDateTime paidAt;

        @JsonProperty("created_at")
        private final LocalDateTime createdAt;

        private final String channel;

        @Enumerated(EnumType.STRING)
        private final PaymentProvider provider;

        public PaymentVerificationData(Builder builder) {
            this.reference = builder.reference;
            this.amount = builder.amount;
            this.currency = builder.currency;
            this.status = builder.status;
            this.gatewayResponse = builder.gatewayResponse;
            this.paidAt = builder.paidAt;
            this.createdAt = builder.createdAt;
            this.channel = builder.channel;
            this.provider = builder.provider;
            this.paymentResponse = builder.paymentResponse;
        }

        // Add getter methods for mapper access
        public String getReference() {
            return reference;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getStatus() {
            return status;
        }

        public PaymentResponse getPaymentResponse() {
            return paymentResponse;
        }

        public String getGatewayResponse() {
            return gatewayResponse;
        }

        public LocalDateTime getPaidAt() {
            return paidAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getChannel() {
            return channel;
        }

        public PaymentProvider getProvider() {
            return provider;
        }

        public void setPaymentResponse(PaymentResponse paymentResponse) {
            this.paymentResponse = paymentResponse;
        }

        public static class Builder {
            private String reference;
            private BigDecimal amount;
            private String currency;
            private String status;
            private String gatewayResponse;
            private LocalDateTime paidAt;
            private LocalDateTime createdAt;
            private String channel;
            private PaymentProvider provider;
            private PaymentResponse paymentResponse;

            public Builder withReference(String reference) {
                this.reference = reference;
                return this;
            }

            public Builder withAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public Builder withCurrency(String currency) {
                this.currency = currency;
                return this;
            }

            public Builder withStatus(String status) {
                this.status = status;
                return this;
            }

            public Builder withGatewayResponse(String gatewayResponse) {
                this.gatewayResponse = gatewayResponse;
                return this;
            }

            public Builder withPaidAt(LocalDateTime paidAt) {
                this.paidAt = paidAt;
                return this;
            }

            public Builder withCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Builder withChannel(String channel) {
                this.channel = channel;
                return this;
            }

            public Builder withProvider(PaymentProvider provider) {
                this.provider = provider;
                return this;
            }

            public Builder withPaymentResponse(PaymentResponse paymentResponse) {
                this.paymentResponse = paymentResponse;
                return this;
            }

            public PaymentVerificationData build() {
                return new PaymentVerificationData(this);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }
}