package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryDTO {
    private Long productId;
    private String productName;
    private String brand;
    private String category;
    private String trackingType;
    private Integer stock;
    private Double purchasePrice;
    private Double sellingPrice;
    private Double stockValue;      // stock * purchasePrice
}