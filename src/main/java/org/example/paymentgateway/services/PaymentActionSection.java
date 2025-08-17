package org.example.paymentgateway.services;

import org.example.paymentgateway.entities.PaymentAudit;
import org.example.paymentgateway.entities.PaymentTransaction;
import org.example.paymentgateway.enums.PaymentProvider;
import org.example.paymentgateway.enums.PaymentStatus;
import org.example.paymentgateway.repositories.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class PaymentActionSection {

    private final Logger log = LoggerFactory.getLogger(PaymentActionSection.class);

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentAuditService paymentAuditService;
    private final PaymentStatusValidator paymentStatusValidator;


    public PaymentActionSection(PaymentTransactionRepository paymentTransactionRepository, PaymentAuditService paymentAuditService, PaymentStatusValidator paymentStatusValidator) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentAuditService = paymentAuditService;
        this.paymentStatusValidator = paymentStatusValidator;
    }

    @Transactional
    public boolean updatePaymentStatus(String reference,
                                       PaymentStatus newStatus,
                                       String paymentEventId,
                                       Object rawData,
                                       PaymentProvider paymentProvider) {
        Optional<PaymentTransaction> paymentTransaction = paymentTransactionRepository.findByReference(reference);

        if (paymentTransaction.isEmpty()) {
            log.info("Payment transaction does not exist: {}", reference);
            throw new IllegalArgumentException("Payment transaction does not exist: " + reference);
        }

        PaymentTransaction transaction = paymentTransaction.get();
        PaymentStatus oldStatus = transaction.getStatus();

        ///  validate and check for both statuses
        if (oldStatus == newStatus) {
            log.info("Payment {} already in status {}", reference, newStatus);
            return true;
        }

        try {
            paymentStatusValidator.validateStatusTransition(oldStatus, newStatus, reference);
            transaction.setStatus(newStatus); /// update if status changed
            transaction.setUpdatedAt(LocalDateTime.now());
            paymentTransactionRepository.save(transaction);

            paymentAuditService.logPaymentAudit(
                    reference,
                    "status updated",
                    paymentEventId,
                    oldStatus,
                    newStatus,
                    paymentProvider,
                    rawData,
                    transaction.getUser().getId().toString()
            );

            log.info("Payment transaction updated: {}", transaction.getReference());
            return true;
        } catch (IllegalStateException e) {
            paymentAuditService.logPaymentAudit(
                    reference,
                    "status invalid",
                    paymentEventId,
                    oldStatus,
                    newStatus,
                    paymentProvider,
                    rawData,
                    transaction.getUser().getId().toString()
            );
            log.error("Invalid payment transaction status: {}", transaction.getStatus());
            return false;
        }

    }
}
