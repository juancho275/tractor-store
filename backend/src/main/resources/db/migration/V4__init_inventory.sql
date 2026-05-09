-- ============================================================
-- V4 - Inventory module schema
-- Stock management per product variant
-- ============================================================

CREATE TABLE inventory_stock (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_variant_id  UUID NOT NULL UNIQUE,
    quantity            INT NOT NULL DEFAULT 0,
    reserved            INT NOT NULL DEFAULT 0,
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_quantity_positive CHECK (quantity >= 0),
    CONSTRAINT chk_reserved_positive CHECK (reserved >= 0),
    CONSTRAINT chk_reserved_lte_quantity CHECK (reserved <= quantity)
);

CREATE INDEX idx_stock_variant ON inventory_stock(product_variant_id);

-- Seed stock for existing variants
INSERT INTO inventory_stock (product_variant_id, quantity, reserved)
SELECT id, 
    CASE 
        WHEN RANDOM() < 0.1 THEN 0      -- 10% agotado
        WHEN RANDOM() < 0.3 THEN 2      -- 20% pocas unidades
        ELSE floor(random() * 50 + 10)  -- 70% buen stock
    END,
    0
FROM catalog_product_variants;