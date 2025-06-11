package org.example.paymentgateway.repositories;

import org.example.paymentgateway.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {


    @Query(value = "SELECT p FROM PaymentTransaction p WHERE p.reference=:reference")
    Optional<PaymentTransaction> findByReference(String reference);
}
