package org.example.paymentgateway.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PayStackResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.example.paymentgateway.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component("payStackAdapter")
public class PayStackServiceAdapter implements PaymentService, PaymentProviderValidator, PaymentProviderHealthChecks {
    private final Logger log = LoggerFactory.getLogger(PayStackServiceAdapter.class);

    @Value("${paystack.secret-key}")
    private String payStackSecretKey;

    @Value("${paystack.url}")
    private String payStackUrl;

    @Value("${paystack.public-key}")
    private String payStackPublicKey;

    private final PayStackService payStackService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final int HEALTH_CHECK_TIME_OUT_COUNT = 5000;
    private static final String HEALTH_CHECK_ENDPOINT = "/bank";


    @Autowired
    public PayStackServiceAdapter(PayStackService payStackService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.payStackService = payStackService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public InitializePaymentResponse createPayment(PaymentRequest request) throws IOException {
        try {

            return payStackService.createPayment(request);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean verification(String paymentId) {
        return payStackService.verification(paymentId);
    }

    @Override
    public PaymentResponse verifyPayment(String paymentId) {
        return payStackService.verifyPayment(paymentId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.info("Initialize payment service adapter");
    }

    @Override
    public void validateConfiguration() {
        if (!StringUtils.hasText(payStackSecretKey)) {
            throw new IllegalStateException("paystack secret key must be valid");
        }

        if (!StringUtils.hasText(payStackPublicKey)) {
            throw new IllegalStateException("paystack secret key must be valid");

        }
        if (!StringUtils.hasText(payStackUrl)) {
            throw new IllegalStateException("paystack secret key must be valid");

        }
    }

    @Override
    public boolean healthChecks() {
        try {
            log.debug("performing pay stack API HEALTH CHECK");
            return pingPayStackAPI();
        } catch (Exception e) {
            log.info("pay stack health check failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean pingPayStackAPI() {
        try {
            String url = payStackUrl + "/balance";
            HttpHeaders headers = createAuthHeaders();

            HttpEntity<String> request = new HttpEntity<>(headers);

            RestTemplate template = createHealthCheckRestTemplate();

            ResponseEntity<String> response = template.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                PayStackResponse payStackResponse = objectMapper.readValue(response.getBody(), PayStackResponse.class);
                return payStackResponse.isStatus();
            }
        } catch (JsonProcessingException e) {
            log.info("payStack balance health check failed  with message {} ", e.getMessage());
        }
        return false;
    }

    private RestTemplate createHealthCheckRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(HEALTH_CHECK_TIME_OUT_COUNT);
        factory.setReadTimeout(HEALTH_CHECK_TIME_OUT_COUNT);

        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    private org.springframework.http.HttpHeaders createAuthHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(payStackSecretKey);
        headers.set("Cache-control", "no-cache");
        return headers;
    }
}
