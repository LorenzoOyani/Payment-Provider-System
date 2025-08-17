package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String paymentId;

    @Basic(optional = false)
    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Basic(optional = false)
    private String reference;

    @Basic(optional = false)
    private LocalDateTime createdAt;

    @Basic(optional = false)
    private String CustomerEmail;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String failureReason;


    @Basic(optional = false)
    private String providerReference;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metaData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public String getReference() {
        return reference;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public User getUser() {
        return user;
    }
}
