export interface SaleItem {
  productId: number;
  quantity: number;
  sellingPrice: number;
  discount: number;
  imeiNumbers: string[];
}

export interface Sale {
  saleDate: string;
  paymentMode: string;
  items: SaleItem[];
}