package org.example.paymentgateway.dto;

import lombok.Builder;
import lombok.Data;
import org.example.paymentgateway.entities.WebhookEventType;
import org.example.paymentgateway.entities.WebhookStatus;
import org.example.paymentgateway.enums.PaymentProvider;

import java.time.LocalDateTime;

@Data
@Builder
public class WebhookEventDto {
    private String eventId;
    private WebhookEventType eventType;
    private PaymentProvider provider;
    private String payload;
    private String signature;
    private String reference;
    private WebhookStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;
    private String errorMessage;
    private Integer retryCount;
}
