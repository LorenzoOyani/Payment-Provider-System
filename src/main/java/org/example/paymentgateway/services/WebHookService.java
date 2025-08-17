package org.example.paymentgateway.services;


import org.example.paymentgateway.dto.WebhookRequest;
import org.example.paymentgateway.dto.WebhookResponse;
import org.example.paymentgateway.enums.PaymentProvider;

public interface WebHookService {




    WebhookResponse processWebhookRequest(WebhookRequest webhookRequest, PaymentProvider paymentProvider,   String eventType);
//
//    void retryWebhook(String eventId);
//
//    Optional<WebhookEvent> getWebhookEvent(String EventId);
}
