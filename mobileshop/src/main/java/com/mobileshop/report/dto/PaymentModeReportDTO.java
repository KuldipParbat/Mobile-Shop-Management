// PaymentModeReportDTO.java
package com.mobileshop.report.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentModeReportDTO {
    private String paymentMode;
    private Long totalOrders;
    private Double totalAmount;
}