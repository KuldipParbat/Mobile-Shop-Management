package com.mobileshop.report.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySalesReportDTO {

    private LocalDate date;

    private Double totalSales;

}