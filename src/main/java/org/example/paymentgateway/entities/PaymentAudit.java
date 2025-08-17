package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_audits")
public class PaymentAudit {


    @Column(name = "payment_id", nullable = false)

    private String payment_id;
    @Basic(optional = false)
    private PaymentStatus old_status;

    @Basic(optional = false)
    private PaymentStatus new_status;

    @Basic(optional = false)
    private String eventType;

    @Basic(optional = false)
    private String providerEventId;

    @CreationTimestamp
    @Column(name = "change_at")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    private String rawData;

    @Basic(optional = false)
    @Column(name = "changed_reason", nullable = false)
    private String changeReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider payment_provider;

    @Column(nullable = false)
    private String userId;

    @Id
    private Long id;

    public PaymentAudit() {

    }

    public Long getId() {
        return id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public PaymentStatus getOld_status() {
        return old_status;
    }

    public PaymentStatus getNew_status() {
        return new_status;
    }

    public String getEventType() {
        return eventType;
    }

    public String getProviderEventId() {
        return providerEventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public String getRawData() {
        return rawData;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public PaymentProvider getPayment_provider() {
        return payment_provider;
    }

    public String getUserId() {
        return userId;
    }


    public PaymentAudit(Builder builder) {
        this.id = builder.id;
        this.payment_id = builder.payment_id;
        this.old_status = builder.old_status;
        this.new_status = builder.new_status;
        this.eventType = builder.eventType;
        this.providerEventId = builder.providerEventId;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.changeReason = builder.changeReason;
        this.payment_provider = builder.payment_provider;
        this.userId = builder.userId;


    }




    public static class Builder {
        private Long id;
        private String payment_id;
        private PaymentStatus old_status;
        private PaymentStatus new_status;
        private String eventType;
        private String providerEventId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String rawData;
        private String changeReason;
        private PaymentProvider payment_provider;
        private String userId;

        private Builder() {

        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder payment_id(String val) {
            payment_id = val;
            return this;
        }

        public Builder oldStatus(PaymentStatus val) {
            old_status = val;
            return this;
        }

        public Builder new_status(PaymentStatus val) {
            new_status = val;
            return this;
        }

        public Builder eventType(String val) {
            eventType = val;
            return this;
        }

        public Builder providerEventId(String val) {
            providerEventId = val;
            return this;
        }

        public Builder createdAt(LocalDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder modifiedAt(LocalDateTime val) {
            modifiedAt = val;
            return this;
        }

        public Builder rawData(String val) {
            rawData = val;
            return this;
        }

        public Builder changeReason(String val) {
            changeReason = val;
            return this;
        }

        public Builder payment_provider(PaymentProvider val) {
            payment_provider = val;
            return this;

        }

        public Builder userId(String val) {
            userId = val;
            return this;
        }

        public PaymentAudit build() {
            return new PaymentAudit(this);
        }

    }

    public static Builder builder() {
        return new Builder();

    }
}
