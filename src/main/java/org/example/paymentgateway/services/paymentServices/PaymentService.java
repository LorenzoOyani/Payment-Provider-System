package org.example.paymentgateway.services.paymentServices;

import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PaymentResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface PaymentService extends InitializingBean {
    InitializePaymentResponse createPayment(PaymentRequest request) throws IOException;

    boolean verification(String paymentId);

    PaymentResponse verifyPayment(String paymentId);
}
