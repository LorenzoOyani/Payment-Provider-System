package org.example.paymentgateway.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.example.paymentgateway.configuration.PayStackProperties;
import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.example.paymentgateway.dto.PaymentResponse;
import org.example.paymentgateway.dto.PaymentVerificationResponse;
import org.example.paymentgateway.entities.*;
import org.example.paymentgateway.enums.Currency;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.example.paymentgateway.exception.PaymentException;
import org.example.paymentgateway.mapper.PaymentMapper;
import org.example.paymentgateway.repositories.PaymentRepository;
import org.example.paymentgateway.repositories.UserRepository;

import org.example.paymentgateway.services.paymentServices.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PayStackService implements PaymentService {


    private static final Logger log = LoggerFactory.getLogger(PayStackService.class);

    private final PaymentRepository paymentRepository;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PayStackProperties properties;
    private final PaymentMapper paymentMapper;

    private static final String INITIALIZE_TRANSACTION = "transactions/initialize";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String AUTHORIZATION_URL = "authorization_url";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "content-Type";

    @Autowired
    public PayStackService(PaymentRepository paymentRepository, OkHttpClient httpClient, ObjectMapper objectMapper,  PayStackProperties properties, PaymentMapper paymentMapper, UserRepository userRepository) {
        if (userRepository == null || paymentRepository == null || httpClient == null || objectMapper == null || properties == null || paymentMapper == null) {
            log.info("empty dependencies...");
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public InitializePaymentResponse createPayment(PaymentRequest request) {
        validatePaymentRequest(request);

        User user = userRepository.findByEmail(request.getCustomerEmail())
                .orElseThrow(() -> {
                    log.error("Failed to find user with email: {}", request.getCustomerEmail());
                    return new UsernameNotFoundException("User not found for email: " + request.getCustomerEmail());
                });

        try {
            int amountInKobo = computeAmountInKobo(request.getAmount());

            final Map<String, Object> paymentReqBody = new HashMap<>();
            paymentReqBody.put("amount", amountInKobo);
            paymentReqBody.put("email", request.getCustomerEmail() != null ? request.getCustomerEmail() : "");
            paymentReqBody.put("metadata", request.getMetadata() != null ? request.getMetadata() : Collections.emptyMap());
            paymentReqBody.put("currency", request.getCurrency());
             paymentReqBody.put("reference", request.getReference());
             paymentReqBody.put("callback_url", request.getGetCallBackUrl());


            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(paymentReqBody),
                    MediaType.parse(CONTENT_TYPE)
            );

            log.info("request processing ...");
            Request requests = new Request.Builder()
                    .url(properties.getUrl() + INITIALIZE_TRANSACTION)
                    .post(body)
                    .addHeader("Authorization", buildBearerToken())
                    .addHeader(AUTHORIZATION_HEADER, buildBearerToken())
                    .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE).build();

            log.info("request processing success.");

            try (Response response = httpClient.newCall(requests).execute()) { ///  a try-with-resources to clean up IO resources
                if (!response.isSuccessful() || response.body() == null) {
                    log.warn("invalid payment request with response code, {}", response.code());
                    throw new PaymentException("invalid payment request with status " + response.code());
                }

                final Map<String, Object> responseBody = objectMapper.readValue(response.body().string(), new TypeReference<Map<String, Object>>() {
                });

                if (responseBody == null || !responseBody.containsKey("data")) {
                    throw new PaymentException("Missing data in response body");
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> paymentData = (Map<String, Object>) responseBody.get("data");
                if (paymentData == null) {
                    throw new PaymentException("invalid payment data");
                }

                @SuppressWarnings(value = "unchecked")
                PaymentDto paymentDto = PaymentDto.builder()
                        .withReference(getSafeFromMap(paymentData, "reference", String.class))
                        .withAmount(getSafeFromMap(paymentData, "amount", BigDecimal.class))
                        .withCustomerEmail(request.getCustomerEmail())
                        .withStatus(PaymentStatus.PENDING)
                        .withProvider(PaymentProvider.PAYSTACK)
                        .withMetaData(getSafeFromMap(paymentData, "metadata", Map.class))
                        .withCurrency(Currency.NGN)
                        .withTransactionId(getSafeFromMap(paymentData, "id",String.class))
                        .withCreatedAt(LocalDateTime.now())
                        .build();


                Payment payment = paymentMapper.toPayment(paymentDto);
                payment.setUser(user);
                paymentRepository.save(payment); /// persist to db;


                return InitializePaymentResponse.builder()
                        .message(getSafeFromMap(paymentData, "message", String.class))
                        .status(PaymentStatus.valueOf("success".equalsIgnoreCase(getSafeFromMap(paymentData, "status", String.class)) ? "success" : "failure"))
                        .authorizationUrl(getSafeFromMap(paymentData, AUTHORIZATION_URL, String.class))
                        .accessCode(getSafeFromMap(paymentData, "access_code", String.class))
                        .reference(getSafeFromMap(paymentData, "reference", String.class))
                        .build();


            }

        } catch (IOException | RuntimeException e) {
            throw new PaymentException("Failed to initialize payment: " + e.getMessage(), e);
        }


    }

    @Override
    public boolean verification(String paymentId) {

        try {
            PaymentResponse response = this.verifyPayment(paymentId);
            return response.isSuccess();
        } catch (PaymentException e) {
            return false;
        }
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request == null || request.getAmount() == null || request.getCustomerEmail() == null) {
            throw new PaymentException("Invalid payment request with amount and customer email required");
        }
    }

    private int computeAmountInKobo(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Amount must be greater than zero.");
        }
        return amount.multiply(BigDecimal.valueOf(100)).intValueExact();

    }


    @SuppressWarnings("unchecked")
    private <T> T getSafeFromMap(Map<String, Object> paymentData, final String reference, Class<T> clazz) {
        if (paymentData == null || !paymentData.containsKey(reference)) { /// !important invariant check.
            return null;
        }
        Object value = paymentData.get(reference);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else if (clazz == BigDecimal.class && value instanceof Number) {
            return (T) BigDecimal.valueOf(((Number) value).doubleValue());

        } else if (clazz == String.class) {
            return (T) value.toString();
        }
        throw new IllegalArgumentException(
                String.format(
                        "Invalid type for key '%s': Expected %s, but got %s", reference, clazz, value.getClass()
                )
        );
    }


    @Override
    public PaymentResponse verifyPayment(String paymentReference) {
        // Validate input
        if (paymentReference == null || paymentReference.isBlank()) {
            throw new PaymentException("Payment reference cannot be empty");
        }

        try {
            Payment payment = paymentRepository.findPaymentByReference(paymentReference)
                    .orElseThrow(() -> new PaymentException("Payment not found with reference: " + paymentReference));

            Request request = new Request.Builder()
                    .url(properties.getUrl() + "/transaction/verify/" + paymentReference)
                    .get()
                    .addHeader(AUTHORIZATION_HEADER, buildBearerToken())
                    .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE).build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new PaymentException("Payment verification failed with status: " + response.code());
                }

                Map<String, Object> responseBody = objectMapper.readValue(
                        response.body().string(),
                        new TypeReference<Map<String, Object>>() {
                        }
                );

                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data == null) {
                    throw new PaymentException("Invalid verification response from provider");
                }

                String status = (String) data.get("status");
                payment.setStatus("success".equalsIgnoreCase(status)
                        ? PaymentStatus.SUCCESS
                        : PaymentStatus.FAILURE);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);
                PaymentVerificationResponse.PaymentVerificationData paymentData=
                PaymentVerificationResponse.PaymentVerificationData.builder()
                        .withChannel((String) data.get("channel"))
                        .withAmount(BigDecimal.valueOf(((Number) data.get("amount")).doubleValue() / 100))
                        .withGatewayResponse((String) data.get("gateway"))
                        .withReference(paymentReference)
                        .withProvider(PaymentProvider.PAYSTACK)
                        .withCurrency("NGN")
                        .withPaidAt(LocalDateTime.now())
                        .withStatus((String) data.get("status"))
                        .withCurrency((String) data.get("currency"))
                        .build();

                return paymentMapper.toPaymentResponse(paymentData);

            }
        } catch (IOException e) {
            log.error("failed with payment verification issues..");
            throw new PaymentException("Failed to verify payment: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new PaymentException("Payment verification error: " + e.getMessage(), e);
        }
    }

    private String buildBearerToken() {
        return "Bearer " + properties.getSecretKey();
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        validatePayStackConfigurations();
        log.info(properties.getUrl());
    }

    private void validatePayStackConfigurations() {
    }
}
