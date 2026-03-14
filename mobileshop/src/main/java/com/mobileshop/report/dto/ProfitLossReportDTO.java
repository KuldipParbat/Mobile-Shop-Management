// ProfitLossReportDTO.java
package com.mobileshop.report.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfitLossReportDTO {
    private LocalDate date;
    private Double totalRevenue;
    private Double totalCost;
    private Double profit;
    private Double margin;
}