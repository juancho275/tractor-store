export interface OrderItemResponse {
  productVariantId: string;
  productName: string;
  variantName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderSummary {
  id: string;
  orderNumber: string;
  customerEmail: string;
  customerName: string;
  items: OrderItemResponse[];
  total: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  shippingAddress: string;
  createdAt: string | null;
}
