CREATE TABLE IF NOT EXISTS payments.payments (
    id         BIGSERIAL      PRIMARY KEY,
    order_id   BIGINT         NOT NULL,
    amount     NUMERIC(12, 2) NOT NULL,
    method     VARCHAR(30)    NOT NULL,
    status     VARCHAR(30)    NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments.payments(order_id);
