package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.example.paymentgateway.enums.PaymentProvider;

import java.time.LocalDateTime;

@Entity
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "webhook_id")
    private String webhookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private PaymentProvider paymentProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private WebhookEventType eventType;


    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

}
