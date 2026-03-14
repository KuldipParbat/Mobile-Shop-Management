export interface Product {
  id?: number;
  name: string;
  brand: string;
  model: string;
  category: string;
  purchasePrice: number;
  sellingPrice: number;
  stock: number;              // ← keep as number (not optional)
  trackingType: 'QUANTITY' | 'IMEI';
}