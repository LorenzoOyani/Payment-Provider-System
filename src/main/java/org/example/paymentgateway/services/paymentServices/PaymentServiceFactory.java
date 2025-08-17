package org.example.paymentgateway.services.paymentServices;

import jakarta.annotation.PostConstruct;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.exception.PaymentException;
import org.example.paymentgateway.services.FlutterWaveAdapter;
import org.example.paymentgateway.services.PayStackServiceAdapter;
import org.example.paymentgateway.repositories.PaymentProviderHealthChecks;
import org.example.paymentgateway.repositories.PaymentProviderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PaymentServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceFactory.class);
    private final PayStackServiceAdapter payStackServiceAdapter;
    private final FlutterWaveAdapter flutterWaveAdapter;

    private final Map<PaymentProvider, PaymentService> providerMap = new ConcurrentHashMap<>();
    private final Map<PaymentProvider, Boolean> providerHealthCheck = new ConcurrentHashMap<>();

    /// A map-registry

    @Autowired
    public PaymentServiceFactory(PayStackServiceAdapter payStackServiceAdapter, FlutterWaveAdapter flutterWaveAdapter) {
        this.payStackServiceAdapter = payStackServiceAdapter;
        this.flutterWaveAdapter = flutterWaveAdapter;
    }

    @PostConstruct
    public void init() {
        log.info("initializing payment services...");
        registerProviders();
        validateProviders();
    }

    private void registerProviders() {
        providerMap.put(PaymentProvider.PAYSTACK, payStackServiceAdapter);
        providerMap.put(PaymentProvider.FLUTTERWAVE, flutterWaveAdapter);

        log.info("registered payment provider with service name, {}",
                providerMap.keySet().stream()
                        .map(PaymentProvider::getName)
                        .collect(Collectors.joining(","))
        );
    }

    public PaymentService getPaymentProviderService(PaymentProvider paymentProvider) {
        if (paymentProvider == null) {
            throw new IllegalArgumentException("paymentProvider cannot be null");
        }

        PaymentService paymentService = providerMap.get(paymentProvider);  ///key-value mapping.
        if (paymentService == null) {
            throw new PaymentException("Unsupported payment provider: " + paymentProvider);
        }
        Boolean isHealthy = providerHealthCheck.get(paymentProvider);
        if (Boolean.FALSE.equals(isHealthy)) {
            log.warn("Payment provider {} is marked as unhealthy, but returning service", paymentProvider);
            throw new IllegalStateException("payment provider health check failed for provider: " + paymentProvider.name());
        }
        return paymentService;
    }

    public void validateProviders() {
        log.info("validating payment providers...");
        final List<String> failedProviders = new ArrayList<>();
        final List<String> successfulProviders = new ArrayList<>();

        for (Map.Entry<PaymentProvider, PaymentService> entry : providerMap.entrySet()) {
            PaymentProvider paymentProvider = entry.getKey();
            PaymentService paymentService = entry.getValue();

            try {
                validateAProviderService(paymentProvider, paymentService);
                successfulProviders.add(paymentProvider.name());
                providerHealthCheck.put(paymentProvider, true);
                log.info("provider {} successfully validated", paymentProvider.name());
            } catch (Exception e) {
                failedProviders.add(paymentProvider.name());
                log.error("failed to validate payment provider: {}", paymentProvider.name(), e);
            }
        }
        if (!successfulProviders.isEmpty()) {
            log.info("Successfully validated providers: {}", String.join(", ", successfulProviders));
        }
        if (!failedProviders.isEmpty()) {
            String errorMessage = String.format(
                    "Payment provider validation failed for: %s. Check configuration and connectivity.",
                    String.join(", ", failedProviders)
            );
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        log.info("successfully validate payment service...");
    }

    private void validateAProviderService(PaymentProvider paymentProvider, PaymentService paymentService) {
        if (paymentService == null) {
            log.info("no such payment service available for provider {}: ", paymentProvider.name());
            throw new IllegalStateException(String.format("no such payment service available for provider %s ", paymentProvider.name()));
        }

        try {
            if (paymentService instanceof PaymentProviderValidator) {
                ((PaymentProviderValidator) paymentService).validateConfiguration();
            }
        } catch (PaymentException e) {
            throw new IllegalStateException(String.format("configuration validation configuration failed for %s, %s", paymentProvider.name(), e.getMessage()), e);
        }

        try {
            if (paymentService instanceof PaymentProviderHealthChecks) {
                boolean isHealthy = ((PaymentProviderHealthChecks) paymentService).healthChecks();
                if (!isHealthy) {
                    throw new IllegalStateException(String.format("health checks failed for provider %s ", paymentProvider.name()));
                }
            }
        } catch (Exception e) {
            log.warn("Health checks failed for provider {}", paymentProvider.name(), e);
            throw new IllegalStateException(
                    String.format("Health checks failed for providers %s: %s",
                            paymentProvider.name(), e.getMessage()), e);
        }
    }

    public Set<PaymentProvider> getAllProviders() {
        return Collections.unmodifiableSet(providerMap.keySet());
    }

    public Set<PaymentProvider> getHealthyProviders() {
        return providerHealthCheck.entrySet().stream()
                .filter(providers -> Boolean.TRUE.equals(providers.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

    }

    public boolean isProviderAvailable(PaymentProvider paymentProvider) {
        return providerMap.containsKey(paymentProvider);
    }

    public int getHealthProviderCount() {
        return (int) providerHealthCheck.values().stream()
                .filter(Boolean.TRUE::equals)
                .count();
    }
}
