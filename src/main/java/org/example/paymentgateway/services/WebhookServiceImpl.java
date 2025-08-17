package org.example.paymentgateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.example.paymentgateway.dto.WebhookEventDto;
import org.example.paymentgateway.dto.WebhookRequest;
import org.example.paymentgateway.dto.WebhookResponse;
import org.example.paymentgateway.entities.WebhookEvent;
import org.example.paymentgateway.entities.WebhookEventType;
import org.example.paymentgateway.entities.WebhookStatus;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.exception.WebhookException;
import org.example.paymentgateway.mapper.WebhookMapper;
import org.example.paymentgateway.repositories.WebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class WebhookServiceImpl implements WebHookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookServiceImpl.class);

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookMapper webhookMapper;
    private final ObjectMapper objectMapper;

    @Value("${paystack.secret-key}")
    private String payStackWebhookSecret;
    @Value("${}")
    private String flutterWaveWebhookSecret;

    @Autowired
    public WebhookServiceImpl(WebhookEventRepository webhookEventRepository, WebhookMapper webhookMapper, ObjectMapper objectMapper) {
        this.webhookEventRepository = webhookEventRepository;
        this.webhookMapper = webhookMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public WebhookResponse processWebhookRequest(WebhookRequest webhookRequest, PaymentProvider paymentProvider, String eventType) {
        log.info("processing webhook for provider: {} with event type: {}", paymentProvider, eventType);
        validateWebhookRequest(webhookRequest);

        Optional<WebhookEvent> existingEvent = Optional.ofNullable(webhookEventRepository.findByEventId(webhookRequest.getEventId()).orElseThrow(() -> {
            log.info("Webhook event not found for id {}", webhookRequest.getEventId());
            return new WebhookException("Webhook event not found for id " + webhookRequest.getEventId());
        }));

        if (existingEvent.isPresent()) {
            log.info("Webhook event found for id {}", webhookRequest.getEventId());
            return createDefaultResponse(existingEvent::get, "Event already processed");
        }

        final WebhookEvent request = createWebhookRequest(webhookRequest, paymentProvider);

        try {
            if (!validateWebhookSignature(request, paymentProvider)) {
                log.warn("Invalid webhook signature:{} for provider", request.getSignature());
                throw new WebhookException("invalid webhook signature");
            }

            String webhookId = extractWebhookRequestId(request.getPayload(), paymentProvider);
            if (isDuplicateWebhookEvent(webhookId)) {
                log.warn("Duplicate webhook event detected with id: {}", webhookId);
                return WebhookResponse.builder()
                        .success(false)
                        .errorMessage("Duplicate event detected")
                        .build();
            }

            JsonNode webhookData = objectMapper.readTree(request.getPayload());

            WebhookEvent webhookEvent = saveWebhookEvent(request.getPayload(), paymentProvider, eventType, webhookId);

            WebhookResponse response = processWebhookEventType(webhookData, paymentProvider, webhookEvent, eventType);


        } catch (Exception ignored) {

        }

    }

    private WebhookResponse processWebhookEventType(JsonNode webhookData, PaymentProvider paymentProvider, WebhookEvent webhookEvent, String eventType) {
    }

    private WebhookEvent saveWebhookEvent(String payload, PaymentProvider paymentProvider, String eventType, String webhookId) {
        WebhookEventDto webhookEventDto = WebhookEventDto.builder()
                .eventId(webhookId)
                .eventType(mapEventType(paymentProvider, eventType))
                .provider(paymentProvider)
                .payload(payload)
                .processedAt(LocalDateTime.now())
                .build();
        return webhookMapper.WebhookEventMapper(webhookEventDto);
    }

    private WebhookEventType mapEventType(PaymentProvider paymentProvider, String eventType) {
        return switch (paymentProvider) {
            case PAYSTACK -> mappedToPayStackEventType(eventType);
            case FLUTTERWAVE -> mappedToFlutterWaveEventType(eventType);
            case null, default -> throw new IllegalArgumentException("unknow provider");

        };
    }

    private boolean isDuplicateWebhookEvent(String webhookId) {
        return webhookEventRepository.findByEventId(webhookId).isPresent();
    }

    private String extractWebhookRequestId(String payload, PaymentProvider paymentProvider) {
        try {
            JsonNode webhookData = objectMapper.readTree(payload);
            return switch (paymentProvider) {
                case PAYSTACK, FLUTTERWAVE -> webhookData.has("id") ? webhookData.get("id").asText() : UUID.randomUUID().toString();
                default -> UUID.randomUUID().toString();
            };
        } catch (Exception e) {

            log.warn("Failed to extract webhook ID, generating random ID", e);
            return UUID.randomUUID().toString();
        }
    }

    private WebhookResponse createDefaultResponse(Supplier<WebhookEvent> eventSupplier, String message) {
        WebhookEvent eventHook = eventSupplier.get();

        return WebhookResponse.builder().eventId(eventHook.getEventId()).message(message).errorMessage(eventHook.getErrorMessage()).status(WebhookStatus.PENDING == eventHook.getStatus() ? WebhookStatus.PENDING : WebhookStatus.PROCESSED).processedAt(LocalDateTime.now()).build();

    }

    private boolean validateWebhookSignature(WebhookEvent request, PaymentProvider paymentProvider) {
        try {
            String secret = getWebhookSecret(paymentProvider);
            if (ObjectUtils.isEmpty(secret)) {
                log.warn("webhook secret not configured for empty secret keys");
                return false;
            }
            return switch (paymentProvider) {
                case PAYSTACK -> validatePaystackSignature(request, secret);
                case FLUTTERWAVE -> validateFlutterWaveSignature(request, secret);
                default -> {
                    log.warn("signature validation failed for providers");
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Error validating webhook signature for provider: {}", paymentProvider, e);
            return false;
        }
    }

    private boolean validateFlutterWaveSignature(WebhookEvent request, String secret) {
    }

    private boolean validatePaystackSignature(WebhookEvent request, String secret) {


    }

    private String getWebhookSecret(PaymentProvider paymentProvider) {
        return switch (paymentProvider) {
            case PAYSTACK -> payStackWebhookSecret;
            case FLUTTERWAVE -> flutterWaveWebhookSecret;
        };
    }

    private WebhookEvent createWebhookRequest(WebhookRequest webhookRequest, PaymentProvider paymentProvider) {
        final String defaultEventName = getDefaultEventName(paymentProvider);

        final WebhookEventType eventType = getDefaultEventType(defaultEventName);

        WebhookEventDto webhookEventDto = WebhookEventDto.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(determineEventType(eventType.toString(), paymentProvider))
                .payload(webhookRequest.getPayload()).provider(paymentProvider)
                .signature(webhookRequest.getSignature())
                .reference(webhookRequest.getReference())
                .status(WebhookStatus.PENDING)
                .receivedAt(LocalDateTime.now())
                .build();

        return webhookMapper.WebhookEventMapper(webhookEventDto);

    }

    private String getDefaultEventName(PaymentProvider paymentProvider) {
        return switch (paymentProvider) {
            case PAYSTACK -> "PAYMENT_PROCESSED";
            case FLUTTERWAVE -> "PAYMENT_SUCCEEDED";
        };

    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T getDefaultEventType(String enumName) {
        T result;
        try {
            result = Enum.valueOf((Class<T>) WebhookEventType.class, enumName);
            return result;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid enum value " + e.getMessage());
        }

    }

    private WebhookEventType determineEventType(String eventType, PaymentProvider paymentProvider) {
        if (eventType == null) {
            return WebhookEventType.PAYMENT_PENDING;
        }

        switch (paymentProvider) {
            case FLUTTERWAVE -> {
                return mappedToFlutterWaveEventType(eventType);
            }
            case PAYSTACK -> {
                return mappedToPayStackEventType(eventType);
            }
            default -> {
                return WebhookEventType.valueOf(eventType.toUpperCase().replace(".", "_"));
            }
        }
    }

    private WebhookEventType mappedToFlutterWaveEventType(String eventType) {
        switch (eventType) {
            case "charge.success", "charge.successful", "payment_success" -> {
                return WebhookEventType.PAYMENT_SUCCEEDED;
            }
            case "charge.failed", "payment_failed" -> {
                return WebhookEventType.PAYMENT_FAILED;
            }
            case "charge.pending", "payment_pending" -> {
                return WebhookEventType.PAYMENT_RECEIVED;
            }
            case "refund.processed", "refund.successful" -> {
                return WebhookEventType.REFUND_PROCESSED;
            }
            case "charge.completed", "payment_completed" -> {
                return WebhookEventType.PAYMENT_COMPLETED;
            }
            default -> {
                return WebhookEventType.PAYMENT_PENDING;
            }
        }
    }

    private WebhookEventType mappedToPayStackEventType(String eventType) {
        switch (eventType) {
            case "charge.completed" -> {
                return WebhookEventType.PAYMENT_COMPLETED;
            }
            case "charge.successful" -> {
                return WebhookEventType.PAYMENT_SUCCEEDED;
            }
            case "charge.failed" -> {
                return WebhookEventType.PAYMENT_FAILED;
            }
            case "refund.successful" -> {
                return WebhookEventType.REFUND_PROCESSED;
            }
            case "charge.pending" -> {
                return WebhookEventType.PAYMENT_RECEIVED;
            }
            default -> {
                return WebhookEventType.PAYMENT_PENDING;
            }
        }
    }

    private WebhookResponse createOptionalResponse(WebhookEvent webhookRequest, @Nullable String message) {
        return WebhookResponse.builder().eventId(webhookRequest.getEventId()).message(message).status(webhookRequest.getStatus()).processedAt(webhookRequest.getProcessedAt()).build();
    }

    private void validateWebhookRequest(WebhookRequest webhookRequest) {
        if (webhookRequest == null) {
            throw new IllegalArgumentException("webhookRequest cannot be null");
        }
        if (webhookRequest.getEventId() == null) {
            throw new IllegalArgumentException("eventId cannot be null");
        }
        if (webhookRequest.getEventType() == null) {
            throw new IllegalArgumentException("eventType cannot be null");
        }
        if (webhookRequest.getPayload() == null) {
            throw new IllegalArgumentException("payload cannot be null");
        }
        if (webhookRequest.getReference() == null) {
            throw new IllegalArgumentException("reference cannot be null");
        }

    }

}
