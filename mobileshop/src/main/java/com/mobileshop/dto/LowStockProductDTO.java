package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LowStockProductDTO {
    private String name;
    private String category;  // will contain "Brand · Category" e.g. "Samsung · Smartphone"
    private Integer stock;
}