-- ============================================================
-- V6 - Order module schema
-- Order lifecycle and items
-- ============================================================

CREATE TABLE order_orders (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number     VARCHAR(50) NOT NULL UNIQUE,
    customer_email   VARCHAR(255) NOT NULL,
    customer_name    VARCHAR(200),
    total            NUMERIC(15,2) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL REFERENCES order_orders(id),
    product_variant_id  UUID NOT NULL,
    product_name        VARCHAR(200) NOT NULL,
    variant_name        VARCHAR(200) NOT NULL,
    unit_price          NUMERIC(15,2) NOT NULL,
    quantity            INT NOT NULL,
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX idx_order_number ON order_orders(order_number);
CREATE INDEX idx_order_email ON order_orders(customer_email);
CREATE INDEX idx_order_status ON order_orders(status);
CREATE INDEX idx_order_item_order ON order_items(order_id);