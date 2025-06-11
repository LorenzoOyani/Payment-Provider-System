CREATE TABLE payment
(
    id                 BIGSERIAL PRIMARY KEY,
    amount             NUMERIC(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    customer_email     VARCHAR(255)   NOT NULL,
    provider           VARCHAR(50)    NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    reference          VARCHAR(255)   NOT NULL,
    created_at         TIMESTAMP      NOT NULL,
    updated_at         TIMESTAMP,
    failure_reason     VARCHAR(20),
    provider_reference VARCHAR(255)   NOT NULL
);

CREATE TABLE payment_audits
(
    id            BIGSERIAL PRIMARY KEY,
    payment_id    BIGINT      NOT NULL,
    old_status    VARCHAR(20) NOT NULL,
    new_status    VARCHAR(20) NOT NULL,
    changed_at    TIMESTAMP   NOT NULL,
    change_reason VARCHAR(20),
    CONSTRAINT fk_payment FOREIGN KEY(payment_id) REFERENCES payment(id)
);

CREATE INDEX idx_payment_id ON payment (id);  -- Fixed table reference (was 'payments')
CREATE INDEX idx_payment_provider_reference ON payment (provider_reference);
CREATE INDEX idx_payment_customer_email ON payment (customer_email);
CREATE INDEX idx_payment_status ON payment (status);
CREATE INDEX idx_payment_audits_payment_id ON payment_audits (payment_id);