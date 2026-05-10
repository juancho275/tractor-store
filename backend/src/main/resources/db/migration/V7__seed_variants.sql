-- ============================================================
-- V7 - Add one standard variant per product without variants
-- Uses full UUID as SKU suffix — 100% unique guaranteed
-- ============================================================

INSERT INTO catalog_product_variants (product_id, sku, name, price, attributes)
SELECT 
    id,
    'VAR-' || REPLACE(id::text, '-', ''),
    name || ' - Estándar',
    base_price,
    '{"tipo": "estandar"}'::jsonb
FROM catalog_products
WHERE id NOT IN (
    SELECT DISTINCT product_id FROM catalog_product_variants
)
AND active = true;

INSERT INTO inventory_stock (product_variant_id, quantity, reserved)
SELECT cpv.id, (floor(random() * 50 + 5))::int, 0
FROM catalog_product_variants cpv
WHERE cpv.id NOT IN (
    SELECT product_variant_id FROM inventory_stock
);