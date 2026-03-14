package com.mobileshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private Double todaySales;

    private Double todayProfit;

    private Long totalProducts;

    private Long totalSuppliers;

    private Long lowStockProducts;

}