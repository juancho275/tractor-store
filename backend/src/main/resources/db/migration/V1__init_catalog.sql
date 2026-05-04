-- ============================================================
-- V1 - Initial schema: Catalog module
-- Creates tables for products, categories and product variants
-- ============================================================

-- Categories
CREATE TABLE catalog_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    image_url   VARCHAR(500),
    parent_id   UUID REFERENCES catalog_categories(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Products
CREATE TABLE catalog_products (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    category_id  UUID NOT NULL REFERENCES catalog_categories(id),
    base_price   NUMERIC(15,2) NOT NULL,
    image_url    VARCHAR(500),
    tags         VARCHAR(500),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Product Variants (color, engine size, etc.)
CREATE TABLE catalog_product_variants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID NOT NULL REFERENCES catalog_products(id),
    sku         VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    price       NUMERIC(15,2) NOT NULL,
    image_url   VARCHAR(500),
    attributes  JSONB,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_products_category ON catalog_products(category_id);
CREATE INDEX idx_products_active ON catalog_products(active);
CREATE INDEX idx_variants_product ON catalog_product_variants(product_id);
CREATE INDEX idx_variants_sku ON catalog_product_variants(sku);

-- Seed data: categories
INSERT INTO catalog_categories (id, name, slug) VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Tractores Clásicos', 'clasicos'),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Autónomos', 'autonomos'),
    ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Cosechadoras', 'cosechadoras'),
    ('d4e5f6a7-b8c9-0123-defa-234567890123', 'Accesorios', 'accesorios');

-- Seed data: products
INSERT INTO catalog_products (id, name, description, category_id, base_price, image_url, tags) VALUES
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        'TractorPro X200',
        'Tractor de alta potencia ideal para terrenos grandes. Motor turbo de 200HP con tracción 4x4.',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        45000000.00,
        'https://images.unsplash.com/photo-1592984791259-da6ea0b2f4ca?w=800',
        'potencia,4x4,turbo'
    ),
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d480',
        'AutoTractor AI-500',
        'Tractor autónomo con IA integrada. GPS de precisión y sensores lidar para navegación sin conductor.',
        'b2c3d4e5-f6a7-8901-bcde-f12345678901',
        120000000.00,
        'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=800',
        'autonomo,ia,gps'
    ),
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d481',
        'HarvestMaster 300',
        'Cosechadora de granos de alta eficiencia. Capacidad de tolva 8000 litros, sistema de trilla axial.',
        'c3d4e5f6-a7b8-9012-cdef-123456789012',
        85000000.00,
        'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800',
        'cosecha,granos,axial'
    );

-- Seed data: variants (UUIDs válidos)
INSERT INTO catalog_product_variants (product_id, sku, name, price, attributes) VALUES
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        'TPX200-RED-4X4',
        'TractorPro X200 - Rojo / 4x4',
        45000000.00,
        '{"color": "Rojo", "traccion": "4x4", "hp": "200"}'
    ),
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        'TPX200-GRN-4X4',
        'TractorPro X200 - Verde / 4x4',
        45000000.00,
        '{"color": "Verde", "traccion": "4x4", "hp": "200"}'
    ),
    (
        'f47ac10b-58cc-4372-a567-0e02b2c3d480',
        'ATA500-STD-AI',
        'AutoTractor AI-500 - Estándar',
        120000000.00,
        '{"color": "Amarillo", "tipo": "autonomo", "sensores": "lidar+gps"}'
    );