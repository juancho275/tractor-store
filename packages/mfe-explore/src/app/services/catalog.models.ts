/**
 * Local models for MFE Explore.
 * Mirror the backend DTOs — keep in sync with API contracts.
 * For cross-MFE shared types use @tractor-store-setup/sharedCatalog
 */

export interface ProductSummary {
  id: string;
  name: string;
  description: string;
  categoryId: string;
  basePrice: number;
  imageUrl: string;
  tags: string;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  imageUrl: string | null;
  parentId: string | null;
}