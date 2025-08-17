package org.example.paymentgateway.controller;

import org.example.paymentgateway.services.PaymentActionSection;
import org.example.paymentgateway.services.PaymentAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
public class PaymentWebhookController {

    private final PaymentActionSection paymentActionSection;
    private final PaymentAuditService paymentAuditService;

    @Value("${paystack.secret-key}")
    private String payStackWebhookSecret;


    @Autowired
    public PaymentWebhookController(PaymentActionSection paymentActionSection, PaymentAuditService paymentAuditService) {
        this.paymentActionSection = paymentActionSection;
        this.paymentAuditService = paymentAuditService;
    }

    public ResponseEntity<?> handlePayStackWebhook(@RequestBody String payload, @RequestHeader("X-paystack-Signature") String signature) {
        if (!verifyPaystackSignature(payload, signature)) {
            return null;
        }
        return null;
    }

    private boolean verifyPaystackSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(payStackWebhookSecret.getBytes(), "HmacSHA256");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static class serviceKey {
        private String type;
        private String algorithm;
        private String originalAlgorithm;

        public serviceKey(String type, String algorithm, boolean intern) {
            this.type = type;
            this.originalAlgorithm = algorithm;
            this.algorithm = intern ? algorithm.intern() : algorithm;
        }

        public int hashCode() {
            return this.type.hashCode() * 31 + this.algorithm.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null) return false;

            return obj instanceof serviceKey other && (this.type.equals(other.type) && (this.algorithm.equals(other.algorithm)));


        }

        public boolean matches(String type, String algorithm) {
            return Objects.equals(this.type, type) && Objects.equals(this.algorithm, algorithm);
        }
    }


}
