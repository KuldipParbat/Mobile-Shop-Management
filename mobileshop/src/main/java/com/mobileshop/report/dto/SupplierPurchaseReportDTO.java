// SupplierPurchaseReportDTO.java
package com.mobileshop.report.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierPurchaseReportDTO {
    private String supplierName;
    private Long totalPurchases;
    private Double totalAmount;
}