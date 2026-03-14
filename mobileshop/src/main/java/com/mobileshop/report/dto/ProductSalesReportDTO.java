package com.mobileshop.report.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductSalesReportDTO {
    private String productName;
    private Long totalSold;
    private Double revenue;
}