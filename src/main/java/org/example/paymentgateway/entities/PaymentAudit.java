package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_audits")
public class PaymentAudit {
    @Id
    private Long id;


    @Column(name = "payment_id", nullable = false)
    private int payment_id;

    @Basic(optional = false)
    private String old_status;

    @Basic(optional = false)
    private String new_status;

    @CreationTimestamp
    private LocalDateTime changed_at;

    @Basic(optional = false)
    private String change_reason;
}
