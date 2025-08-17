package org.example.paymentgateway.repositories;

import org.example.paymentgateway.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByReference(String reference);
}
