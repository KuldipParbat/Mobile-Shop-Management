// WeeklySummaryReportDTO.java
package com.mobileshop.report.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklySummaryReportDTO {
    private String week;
    private Double totalSales;
    private Double totalProfit;
    private Long totalOrders;
}