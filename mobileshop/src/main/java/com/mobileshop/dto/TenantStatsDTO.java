package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TenantStatsDTO {
    private Long   id;
    private String tenantId;
    private String shopName;
    private String ownerName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String gstNumber;
    private boolean active;
    private Long   totalUsers;
    private Long   totalProducts;
    private Long   totalSales;
}