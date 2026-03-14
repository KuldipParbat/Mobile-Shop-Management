export interface PurchaseItem {
  productId: number;
  quantity: number;
  purchasePrice: number;
  imeiNumbers: string[];
}

export interface Purchase {
  supplierId: number;
  invoiceNumber: string;
  purchaseDate: string;
  items: PurchaseItem[];
}