package org.example.paymentgateway.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentgateway.entities.PaymentAudit;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.example.paymentgateway.repositories.PaymentAuditRepository;
import org.example.paymentgateway.repositories.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentAuditService {

    Logger log = LoggerFactory.getLogger(PaymentAuditService.class);

    private final ObjectMapper objectMapper;
    private final PaymentAuditRepository paymentAuditRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public PaymentAuditService(ObjectMapper objectMapper, PaymentAuditRepository paymentAuditRepository, PaymentTransactionRepository paymentTransactionRepository) {
        this.objectMapper = objectMapper;
        this.paymentAuditRepository = paymentAuditRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Transactional
    public PaymentAudit logPaymentAudit(String paymentId,
                                        String eventType,
                                        String providerEventId,
                                        PaymentStatus newStatus,
                                        PaymentStatus oldStatus,
                                        PaymentProvider paymentProvider,
                                        Object rawData,
                                        String userId) {

        ///  check if a provider event has already taken place(Idempotency);
        if (providerEventId != null) {
            Optional<PaymentAudit> existingProviderEvent = Optional.ofNullable(paymentAuditRepository.findByPaymentIdAndProviderEventId(paymentId, providerEventId));

            if (existingProviderEvent.isPresent()) {
                log.info("Payment audit for provider event {} already exists", providerEventId);
                return existingProviderEvent.get();
            }
        }

        try {

            String rawDataString = rawData != null ? objectMapper.writeValueAsString(rawData) : null;
            PaymentAudit payAuditLog = PaymentAudit.builder()
                    .payment_id(paymentId)
                    .eventType(eventType)
                    .oldStatus(oldStatus)
                    .new_status(newStatus)
                    .providerEventId(providerEventId)
                    .rawData(rawDataString)
                    .createdAt(LocalDateTime.now())
                    .userId(userId)
                    .build();

            log.info("Payment audit for provider event {} created ", providerEventId);
            return paymentAuditRepository.save(payAuditLog);
        } catch (JsonProcessingException e) {

            log.error("Failed to serialize raw data for payment payment {}: {}", paymentId, e.getMessage());
            throw new RuntimeException("Failed to create Audit log", e);
        }

    }

//    public boolean updatePaymentStatus(
//            String paymentReference,
//            PaymentStatus newStatus,
//            PaymentProvider provider,
//            String providerEventId,
//            Map<String, Object> rawData
//    ) {
//        Optional<PaymentTransaction> transaction =
//                paymentTransactionRepository.findByReference(paymentReference);
//        if (transaction.isEmpty()) {
//            log.error("payment transaction nt found with reference {}", paymentReference);
//            throw new IllegalArgumentException("payment transaction nt found with reference " + paymentReference);
//        }
//
//        PaymentTransaction paymentTransaction = transaction.get();
//        PaymentStatus oldStatus = paymentTransaction.getStatus(); /// previous status
//        if (oldStatus == newStatus) {
//            log.info("payment status with db status {} with reference {} already exists",oldStatus, paymentReference);
//            return true;
//
//        }
//
//        if (!isValidStatusTransition(oldStatus, newStatus)) {
//            log.warn(
//                    "Invalid status transition for payment {}: {} -> {}",
//                    paymentReference, oldStatus, newStatus
//            );
//
//             this.logPaymentAudit(
//                    paymentReference,
//                    "invalid_status_transition",
//                    PaymentStatus.valueOf(providerEventId),
//                    oldStatus,
//                    newStatus,
//                    provider,
//                    String.valueOf(rawData),
//                    LocalDateTime.now(),
//                    transaction.get().getUser().getId().toString()
//
//            );
//            return false;
//        }
//
//
//        return false;
//    }

    private boolean isValidStatusTransition(PaymentStatus oldStatus, PaymentStatus newStatus) {
        switch (oldStatus) {
            case PENDING -> {
                return newStatus == PaymentStatus.SUCCESS || newStatus == PaymentStatus.FAILURE || newStatus == PaymentStatus.CANCELLED;
            }
            case SUCCESS -> {
                return newStatus == PaymentStatus.REFUNDED;
            }
            case FAILURE -> {
                return newStatus == PaymentStatus.PENDING || newStatus == PaymentStatus.CANCELLED; /// ALLOW FOR RETRY OR CANCEL ALL TOGETHER
            }
            default -> {
                return false;
            }
        }
    }
}
