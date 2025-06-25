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

@Component
public class PaymentServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceFactory.class);
    private final PayStackServiceAdapter payStackServiceAdapter;
    private final FlutterWaveAdapter flutterWaveAdapter;

    private Map<PaymentProvider, PaymentService> map;

    /// A map-registry

    @Autowired
    public PaymentServiceFactory(PayStackServiceAdapter payStackServiceAdapter, FlutterWaveAdapter flutterWaveAdapter) {
        this.payStackServiceAdapter = payStackServiceAdapter;
        this.flutterWaveAdapter = flutterWaveAdapter;
    }

    @PostConstruct
    public void init() {
        map = new HashMap<>();
        map.put(PaymentProvider.PAYSTACK, payStackServiceAdapter);
        map.put(PaymentProvider.FLUTTERWAVE, flutterWaveAdapter);
    }

    public PaymentService getPaymentProviderService(PaymentProvider paymentProvider) {
        PaymentService paymentService = map.get(paymentProvider);  ///key-value mapping.
        if (paymentService == null) {
            throw new PaymentException("no such payment provider: " + paymentProvider);
        }

        return paymentService;
    }

    public void validateProviders() {
        log.info("validating payment providers...");
        final List<String> failedProviders = new ArrayList<>();
        final List<String> successfulProviders = new ArrayList<>();

        for (Map.Entry<PaymentProvider, PaymentService> entry : map.entrySet()) {
            PaymentProvider paymentProvider = entry.getKey();
            PaymentService paymentService = entry.getValue();

            try {
                validateAProviderService(paymentProvider, paymentService);
                successfulProviders.add(paymentProvider.name());
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
        }
    }
}
