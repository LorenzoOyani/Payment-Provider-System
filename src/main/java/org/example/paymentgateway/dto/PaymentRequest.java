package org.example.paymentgateway.dto;

import org.example.paymentgateway.enums.Currency;
import org.example.paymentgateway.entities.Customer;
import org.example.paymentgateway.enums.PaymentProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class PaymentRequest {

    private BigDecimal amount;
    private String customerEmail;
    private Map<String, Object> metadata;
    private String reference;
    private Currency currency;
    private Map<String, Object> providerMetadata;
    private String callBackUrl;
    private PaymentProvider paymentProvider;
    private Customer customer;
    private LocalDateTime createdAt;



    // Getters
    public BigDecimal getAmount() {
        return amount;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public String getReference() {
        return reference;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setReference(String reference){
        this.reference = reference;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Map<String, Object> getProviderMetadata() {
        return providerMetadata;
    }

    public void setProviderMetadata(Map<String, Object> providerMetadata) {
        this.providerMetadata = providerMetadata;
    }

    public String getGetCallBackUrl() {
        return callBackUrl;
    }

    public void setGetCallBackUrl(String getCallBackUrl) {
        this.callBackUrl = getCallBackUrl;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
