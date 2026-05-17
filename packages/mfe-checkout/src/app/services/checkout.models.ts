export interface CartItem {
  id: string;
  productId: string;
  productVariantId: string;
  productName: string;
  variantName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  imageUrl: string;
}

export interface Cart {
  id: string;
  sessionId: string;
  items: CartItem[];
  itemCount: number;
  total: number;
  status: string;
}

export interface OrderRequest {
  customerEmail: string;
  customerName: string;
  shippingAddress: string;
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  productVariantId: string;
  productName: string;
  variantName: string;
  unitPrice: number;
  quantity: number;
}

export interface OrderItemResponse {
  productVariantId: string;
  productName: string;
  variantName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderResponse {
  id: string;
  orderNumber: string;
  customerEmail: string;
  customerName: string;
  items: OrderItemResponse[];
  total: number;
  status: string;
  shippingAddress: string;
  createdAt: string | null;
}