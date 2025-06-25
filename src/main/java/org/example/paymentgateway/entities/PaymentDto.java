package org.example.paymentgateway.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import org.example.paymentgateway.configuration.DataBaseConverters;
import org.example.paymentgateway.enums.Currency;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


public class PaymentDto {
    private Long paymentId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentProvider provider;
    private PaymentStatus status;
    private String reference;
    private String customerEmail;
    private String providerReference;
    @Convert(converter = DataBaseConverters.class)
    @Column(columnDefinition = "Text")
    private Map<String, Object> metaData;
    private User user;
    @CreationTimestamp
    private LocalDateTime created_at;
    @UpdateTimestamp
    private LocalDateTime updated_at;

    public PaymentDto() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getReference() {
        return reference;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public static Builder builder() {
        return new Builder();

    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public static class Builder {
        private BigDecimal amount;
        private Currency currency;
        private PaymentProvider provider;
        private PaymentStatus status;
        private String reference;
        private String customerEmail;
        private String providerReference;
        private Map<String, Object> metaData;
        private User user;

        public Map<String, Object> getMetaData() {
            return metaData;
        }

        public void setMetaData(Map<String, Object> metaData) {
            this.metaData = metaData;
        }


        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder withProvider(PaymentProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder withStatus(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder withCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public Builder withProviderReference(String providerReference) {
            this.providerReference = providerReference;
            return this;
        }

        public Builder withMetaData(Map<String, Object> metaData) {
            this.metaData = metaData;
            return this;
        }

        public PaymentDto build() {
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setAmount(this.amount);
            paymentDto.setCurrency(this.currency);
            paymentDto.setProvider(this.provider);
            paymentDto.setStatus(this.status);
            paymentDto.setReference(this.reference);
            paymentDto.setCustomerEmail(this.customerEmail);
            paymentDto.setProviderReference(this.providerReference);
            paymentDto.setMetaData(this.metaData);
            return paymentDto;

        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {

            this.user = user;
        }
    }
}
