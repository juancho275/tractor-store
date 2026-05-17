export const PRODUCT_ID   = 'b1a2c3d4-0000-0000-0000-000000000001';
export const VARIANT_ID   = 'b1a2c3d4-0000-0000-0000-000000000002';
export const SESSION_ID   = 'sess-e2e-test-abc123';
export const USER_EMAIL   = 'e2e@tractorstore.com';
export const USER_TOKEN   = 'eyJhbGciOiJIUzI1NiJ9.e2etest.signature';

export const products = {
  content: [{
    id: PRODUCT_ID,
    name: 'TractorPro X200',
    description: 'Tractor de alta potencia para agricultura moderna',
    categoryId: 'cat-001',
    basePrice: 45000000,
    imageUrl: 'https://images.unsplash.com/photo-1592984791259-da6ea0b2f4ca?w=400',
    tags: 'tractor,agricola',
  }],
  totalElements: 1, totalPages: 1, number: 0, size: 12,
};

export const categories = [
  { id: 'cat-001', name: 'Tractores', slug: 'tractores', imageUrl: null, parentId: null },
];

export const productDetail = {
  id: PRODUCT_ID,
  name: 'TractorPro X200',
  description: 'Tractor de alta potencia para agricultura moderna',
  categoryId: 'cat-001',
  basePrice: 45000000,
  imageUrl: 'https://images.unsplash.com/photo-1592984791259-da6ea0b2f4ca?w=400',
  tags: 'tractor', active: true,
  variants: [{
    id: VARIANT_ID, sku: 'TP-X200-STD', name: 'Estándar',
    price: 45000000, imageUrl: null,
    attributes: { tipo: 'Estándar' }, active: true,
  }],
};

export const stock = {
  productVariantId: VARIANT_ID,
  quantity: 10, reserved: 0, available: 10, status: 'IN_STOCK',
};

export const authResponse = {
  token: USER_TOKEN, email: USER_EMAIL, role: 'USER', expiresIn: 86400000,
};

export const emptyCart = {
  id: 'cart-001', sessionId: SESSION_ID,
  items: [], itemCount: 0, total: 0, status: 'ACTIVE',
};

export const cartWithItem = {
  id: 'cart-001', sessionId: SESSION_ID,
  items: [{
    id: 'item-001', productId: PRODUCT_ID, productVariantId: VARIANT_ID,
    productName: 'TractorPro X200', variantName: 'Estándar',
    unitPrice: 45000000, quantity: 1, subtotal: 45000000, imageUrl: null,
  }],
  itemCount: 1, total: 45000000, status: 'ACTIVE',
};

export const order = {
  id: 'order-e2e-001', orderNumber: 'TS-99999-2001',
  customerEmail: USER_EMAIL, customerName: 'E2E Tester',
  items: [{
    productVariantId: VARIANT_ID, productName: 'TractorPro X200',
    variantName: 'Estándar', unitPrice: 45000000, quantity: 1, subtotal: 45000000,
  }],
  total: 45000000, status: 'PENDING',
  shippingAddress: 'Calle 123 #45-67, Cali, Valle del Cauca',
  createdAt: '2026-05-17T00:00:00',
};
