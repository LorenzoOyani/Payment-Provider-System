package org.example.paymentgateway.dto;

import lombok.Builder;
import org.example.paymentgateway.entities.WebhookStatus;

import java.time.LocalDateTime;

@Builder
public class WebhookResponse {
    private String eventId;
    private WebhookStatus status;
    private String message;
    private LocalDateTime processedAt;
    private String errorMessage;
    private boolean success;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public WebhookStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
