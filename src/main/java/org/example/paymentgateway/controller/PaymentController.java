package org.example.paymentgateway.controller;

import org.example.paymentgateway.dto.InitializePaymentResponse;
import org.example.paymentgateway.dto.PaymentRequest;
import org.example.paymentgateway.exception.PaymentException;
import org.example.paymentgateway.services.paymentServices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceImpl paymentServiceImpl;


    public PaymentController(PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;
    }


    @PostMapping("/initialize")
    public ResponseEntity<InitializePaymentResponse> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            InitializePaymentResponse response = paymentServiceImpl.createPayment(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("initiatePayment error", e);
            throw new PaymentException("initiatePayment error ", e);
        }
    }
}
