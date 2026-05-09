-- ============================================================
-- V5 - Cart module schema
-- Shopping cart sessions and items
-- ============================================================

CREATE TABLE cart_carts (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id     VARCHAR(255) NOT NULL UNIQUE,
    customer_email VARCHAR(255),
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at     TIMESTAMP NOT NULL DEFAULT NOW() + INTERVAL '24 hours'
);

CREATE TABLE cart_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id             UUID NOT NULL REFERENCES cart_carts(id) ON DELETE CASCADE,
    product_id          UUID NOT NULL,
    product_variant_id  UUID NOT NULL,
    product_name        VARCHAR(200) NOT NULL,
    variant_name        VARCHAR(200) NOT NULL,
    unit_price          NUMERIC(15,2) NOT NULL,
    quantity            INT NOT NULL DEFAULT 1,
    image_url           VARCHAR(500),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX idx_cart_session ON cart_carts(session_id, status);
CREATE INDEX idx_cart_item_cart ON cart_items(cart_id);