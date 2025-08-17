package org.example.paymentgateway.dto;

import org.example.paymentgateway.enums.Currency;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.entities.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentTransactionsDto {
    private Long id;
    private String reference;
    private User user;
    private BigDecimal amount;
    private Currency currency;
    private String description;
    private PaymentProvider paymentProvider;
    private String transactionId;
    private String paymentUrl;
    private String status;
    private LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String reference;
        private User user;
        private BigDecimal amount;
        private Currency currency;
        private String description;
        private PaymentProvider paymentProvider;
        private String transactionId;
        private String paymentUrl;
        private String status;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder paymentProvider(PaymentProvider paymentProvider) {
            this.paymentProvider = paymentProvider;
            return this;
        }
        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        public Builder paymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public PaymentTransactionsDto build() {
            return new PaymentTransactionsDto();
        }
    }

}
