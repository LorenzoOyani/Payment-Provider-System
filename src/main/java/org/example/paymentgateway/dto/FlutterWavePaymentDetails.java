package org.example.paymentgateway.dto;

import lombok.Data;
import org.example.paymentgateway.entities.Customer;
import org.example.paymentgateway.entities.PaymentStatus;

public class FlutterWavePaymentDetails {
    private String link; /// link for redirection
    private String txRef;
    private PaymentStatus status;
    private String paymentType;
    private String createdAt;
    private String accountId;
    private Customer customer;



    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTxRef() {
        return txRef;
    }

    public void setTxRef(String txRef) {
        this.txRef = txRef;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
