package org.example.paymentgateway.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "permission")
public class Permission {
    @Id
    private Long id;

    @Column(unique = true)
    private String name;

    private String resource;
    private String action;
    private String description;

    @Column(name = "transaction_limit")
    private BigDecimal transactionLimit;

    @Column(name = "daily_limit")
    private BigDecimal dailyLimit;

    public String getName() {
        return name;
    }
}
