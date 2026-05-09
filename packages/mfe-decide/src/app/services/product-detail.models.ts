export interface ProductVariant {
  id: string;
  sku: string;
  name: string;
  price: number;
  imageUrl?: string;
  attributes: Record<string, string>;
  active: boolean;
}

export interface ProductDetail {
  id: string;
  name: string;
  description: string;
  categoryId: string;
  basePrice: number;
  imageUrl: string;
  tags: string;
  active: boolean;
  variants: ProductVariant[];
}

export interface StockInfo {
  productVariantId: string;
  quantity: number;
  reserved: number;
  available: number;
  status: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
}