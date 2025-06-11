package org.example.paymentgateway.services;

import jakarta.annotation.PostConstruct;
import org.example.paymentgateway.entities.PaymentProvider;
import org.example.paymentgateway.exception.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentServiceFactory {
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
    }
}
