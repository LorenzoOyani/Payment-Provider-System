package org.example.paymentgateway.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_audits")
public class PaymentAudit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;


    @Column(name = "payment_id", nullable = false)
    private int payment_id;

    @Basic(optional = false)
    private String old_status;

    @Basic(optional = false)
    private String new_status;

    @CreationTimestamp
    @Column(name = "change_at")
    private LocalDateTime changedAt;

    @Basic(optional = false)
    @Column(name = "changed_reason", nullable = false)
    private String changeReason;

}
