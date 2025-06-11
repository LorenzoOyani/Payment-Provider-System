package org.example.paymentgateway.services;

import org.example.paymentgateway.configuration.FlutterProperties;
import org.example.paymentgateway.dto.*;
import org.example.paymentgateway.entities.PaymentProvider;
import org.example.paymentgateway.entities.PaymentStatus;
import org.example.paymentgateway.exception.PaymentException;
import org.example.paymentgateway.exception.PaymentVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class FlutterWaveService implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(FlutterWaveService.class);

    private final RestTemplate restTemplate;
    private final FlutterProperties flutterProperties;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Autowired
    public FlutterWaveService(RestTemplate restTemplate, FlutterProperties flutterProperties) {
        this.restTemplate = restTemplate;
        this.flutterProperties = flutterProperties;
    }

    @Override
    public InitializePaymentResponse createPayment(PaymentRequest request) throws IOException {
        log.info("Starting createPayment process...");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set(AUTHORIZATION_HEADER, "Bearer " + flutterProperties.getSecretKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = buildPayload(request);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

        try {
            log.info("Sending request to FlutterWave API...");
            ResponseEntity<FlutterWaveResponse> responseEntity = restTemplate.exchange(
                    flutterProperties.getBaseUrl() + "/payments",
                    HttpMethod.POST,
                    httpEntity,
                    FlutterWaveResponse.class
            );

            log.info("Request to FlutterWave API successful. Handling response...");
            return handleResponse(responseEntity);

        } catch (Exception e) {
            log.warn("Exception occurred during createPayment: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean verification(String paymentId) {
        try {
            PaymentResponse response = this.verifyPayment(paymentId);
            return response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> buildPayload(PaymentRequest request) {
//        log.info("Building payload for createPayment...");
        Map<String, Object> payload = new HashMap<>();
        payload.put("tx_ref", request.getReference());
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency().name());
        payload.put("redirect_url", request.getGetCallBackUrl());

        Map<String, Object> customerPayload = new HashMap<>();
        customerPayload.put("email", request.getCustomer().getEmail());
        customerPayload.put("name", request.getCustomer().getName());
        customerPayload.put("phoneNumber", request.getCustomer().getPhoneNumber());

        payload.put("customer", customerPayload);

        if (request.getMetadata() != null) {
            payload.put("metadata", request.getMetadata());
        }
//        log.info("Payload built successfully: " + payload);
        return payload;

    }

    private PaymentStatus mapStatus(String paymentStatus) {
//        log.info("Mapping payment status: " + paymentStatus);
        return switch (paymentStatus) {
            case "success" -> PaymentStatus.SUCCESS;
            case "failed" -> PaymentStatus.FAILURE;
            case "pending" -> PaymentStatus.PENDING;
            default -> {
//                log.warn("Unknown payment status encountered: " + paymentStatus);
                throw new PaymentException("Unknown payment status " + paymentStatus);
            }
        };
    }

    private InitializePaymentResponse handleResponse(ResponseEntity<FlutterWaveResponse> responseEntity) {
        log.info("Handling response from FlutterWave...");
        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            log.warn("Invalid response received from FlutterWave.");
            throw new PaymentException("Invalid response from flutterWave");
        }
        FlutterWaveResponse response = responseEntity.getBody();
        if (!"success".equalsIgnoreCase(response.getStatus())) {
//            log.warn("FlutterWave payment failed with message: " + response.getMessage());
            throw new PaymentException("flutterWave payment failed with  " + response.getMessage());
        } else {

//        log.info("Successfully processed response. Building InitializePaymentResponse...");
            return InitializePaymentResponse.builder()
                    .authorizationUrl(response.getDetails().getLink())
                    .reference(response.getDetails().getTxRef())
                    .provider(PaymentProvider.FLUTTERWAVE)
                    .status(mapStatus(response.getDetails().getStatus().name()))
                    .build();
        }
    }

    @Override
    public PaymentResponse verifyPayment(String paymentId) {
//        log.info("Starting verifyPayment process with paymentId: " + paymentId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set(AUTHORIZATION_HEADER, buildUpToken());
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        try {
//            log.info("Sending request to FlutterWave API...");
            ResponseEntity<FlutterWaveResponse> responseEntity = restTemplate.exchange(
                    flutterProperties.getBaseUrl() + "/transactions/verify/" + paymentId,
                    HttpMethod.GET,
                    httpEntity,
                    FlutterWaveResponse.class

            );
//            log.info("Request to FlutterWave API successful. Handling response...");
            return handlePaymentResponse(responseEntity);
        } catch (Exception e) {
            throw new PaymentVerificationException("failed to verify payment with error message " + e.getMessage());
        }
    }

    private PaymentResponse handlePaymentResponse(ResponseEntity<FlutterWaveResponse> responseEntity) {
        /// check and invalidate the invariant.
        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new PaymentException("Invalid response from flutterWave");
        }
        FlutterWaveResponse response = responseEntity.getBody();
        if (!"success".equalsIgnoreCase(response.getStatus())) {
            throw new PaymentException("flutterWave payment failed with  " + response.getMessage());
        } else {
            log.info("successfully payment response with status... {}", response.getStatus());
            return PaymentResponse.builder().isSuccess(true)
                    .message("successfully verified payment")
                    .reference(response.getDetails().getTxRef())
                    .status(mapStatus(response.getDetails().getStatus().name()))
                    .updated_at(LocalDateTime.now())
                    .build();
        }
    }

    private String buildUpToken() {
        return BEARER + " " + flutterProperties.getSecretKey();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("");
    }
}




