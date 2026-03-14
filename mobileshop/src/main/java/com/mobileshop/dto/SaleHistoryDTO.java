package com.mobileshop.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SaleHistoryDTO {
    private Long id;
    private LocalDate saleDate;
    private String paymentMode;
    private Double totalAmount;
    private Integer itemCount;
}