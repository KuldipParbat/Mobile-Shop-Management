package com.mobileshop.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseHistoryDTO {
    private Long id;
    private LocalDate purchaseDate;
    private String invoiceNumber;
    private String supplierName;
    private Integer itemCount;
    private Double totalAmount;  // ← add this
}