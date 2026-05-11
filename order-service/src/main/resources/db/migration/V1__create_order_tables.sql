CREATE TABLE IF NOT EXISTS orders.customers (
    id        BIGSERIAL    PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email     VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orders.orders (
    id           BIGSERIAL      PRIMARY KEY,
    customer_id  BIGINT         NOT NULL REFERENCES orders.customers(id),
    total_amount NUMERIC(12, 2) NOT NULL,
    status       VARCHAR(30)    NOT NULL,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orders.order_items (
    id            BIGSERIAL      PRIMARY KEY,
    order_id      BIGINT         NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
    product_id    BIGINT         NOT NULL,
    product_title VARCHAR(255)   NOT NULL,
    quantity      INTEGER        NOT NULL,
    unit_price    NUMERIC(12, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id    ON orders.orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id  ON orders.order_items(order_id);
