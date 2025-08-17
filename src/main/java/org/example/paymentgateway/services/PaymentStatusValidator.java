package org.example.paymentgateway.services;

import org.example.paymentgateway.enums.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusValidator {

    private static final Logger log = LoggerFactory.getLogger(PaymentStatusValidator.class);

    public boolean isValidStatusTransition(PaymentStatus oldStatus, PaymentStatus newStatus) {
        if(oldStatus == newStatus) {
            return true;
        }

            return switch (oldStatus){
                case PENDING -> newStatus == PaymentStatus.SUCCESS ||
                newStatus == PaymentStatus.FAILURE;
                case SUCCESS -> newStatus == PaymentStatus.REFUNDED;
                case FAILURE -> newStatus == PaymentStatus.REFUNDED ||
                        newStatus == PaymentStatus.CANCELLED;
                default -> false;
        };

    }

    public void validateStatusTransition(PaymentStatus oldStatus, PaymentStatus newStatus, String reference) {
        if(!isValidStatusTransition(oldStatus, newStatus)) {
            log.warn("Invalid status transition for payment, {}, {}, {}: " , oldStatus, newStatus, reference);
            throw new IllegalStateException("Invalid status transition for payment, " + oldStatus + ", " + newStatus + ", " + reference);
        }
    }
}
