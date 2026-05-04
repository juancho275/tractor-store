// ============================================
// SHARED CATALOG — Modelos de dominio compartidos
// Usado por todos los MFEs para garantizar
// consistencia de tipos entre micro-frontends
// ============================================

/** Variante de producto (color, motor, etc.) */
export interface ProductVariant {
  id: string;
  sku: string;
  name: string;
  price: number;
  stock: number;
  attributes: Record<string, string>;
  imageUrl?: string;
}

/** Modelo principal de producto */
export interface Product {
  id: string;
  name: string;
  description: string;
  category: string;
  basePrice: number;
  imageUrl: string;
  variants: ProductVariant[];
  tags: string[];
}

/** Categoria del catalogo */
export interface Category {
  id: string;
  name: string;
  slug: string;
  imageUrl?: string;
  parentId?: string;
}

/** Item dentro del carrito */
export interface CartItem {
  id: string;
  productId: string;
  variantId: string;
  productName: string;
  variantName: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

/** Estado del carrito */
export interface Cart {
  id: string;
  items: CartItem[];
  total: number;
  itemCount: number;
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'CANCELLED';

/** Orden de compra */
export interface Order {
  id: string;
  orderNumber: string;
  items: CartItem[];
  total: number;
  status: OrderStatus;
  createdAt: string;
  customerEmail: string;
}

// ============================================
// EVENTOS — Contrato de comunicacion entre MFEs
// ============================================

export interface CartUpdatedPayload {
  count: number;
  total: number;
}

export interface ProductSelectedPayload {
  productId: string;
  productName: string;
}

export interface VariantSelectedPayload {
  productId: string;
  variantId: string;
  sku: string;
  price: number;
}

/**
 * Eventos tipados del sistema de micro-frontends.
 * Usar siempre estas constantes para garantizar
 * consistencia en la comunicacion entre MFEs.
 *
 * @example
 * document.dispatchEvent(
 *   new CustomEvent<CartUpdatedPayload>(MFEEvents.CART_UPDATED, {
 *     detail: { count: 3, total: 299.99 },
 *     bubbles: true,
 *     composed: true
 *   })
 * );
 */
export const MFEEvents = {
  CART_UPDATED:     'checkout:cart-updated',
  PRODUCT_SELECTED: 'explore:product-selected',
  VARIANT_SELECTED: 'decide:variant-selected',
  CATEGORY_CHANGED: 'explore:category-changed',
} as const;

export type MFEEventName = typeof MFEEvents[keyof typeof MFEEvents];
