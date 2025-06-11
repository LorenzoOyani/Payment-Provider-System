package org.example.paymentgateway.services;

import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.example.paymentgateway.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("flutterWave")
public class FlutterWaveAdapter implements PaymentService {

    private final FlutterWaveService flutterWaveService;

    @Autowired
    public FlutterWaveAdapter(FlutterWaveService flutterWaveService) {
        this.flutterWaveService = flutterWaveService;
    }

    @Override
    public InitializePaymentResponse createPayment(PaymentRequest request) throws IOException {
        return flutterWaveService.createPayment(request);
    }

    @Override
    public boolean verification(String paymentId) {
        return flutterWaveService.verification(paymentId);
    }

    @Override
    public PaymentResponse verifyPayment(String paymentId) {
        return flutterWaveService.verifyPayment(paymentId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
